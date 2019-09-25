package zedi.pacbridge.app.net;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.Constants;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.net.annotations.ConnectionClosed;
import zedi.pacbridge.net.annotations.ConnectionIdentityChanged;
import zedi.pacbridge.net.annotations.ConnectionUnexpectedlyClosed;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public class IncomingOnlyConnectionManager extends BaseConnectionManager implements ConnectionManager, Notifiable {
    private static Logger logger = LoggerFactory.getLogger(IncomingOnlyConnectionManager.class.getName());
    
    public static final String TIMEOUT_SCAN_TIME_PROPERTY_NAME = "incomingOnlyConnectionManager.inactiveScanTimeSeconds";
    public static final Integer DEFAULT_SCAN_TIME_SECONDS = 60;
    
    private static IntegerSystemProperty scanTimeSecondsProperty = new IntegerSystemProperty(TIMEOUT_SCAN_TIME_PROPERTY_NAME, DEFAULT_SCAN_TIME_SECONDS);
    
    private Map<SiteAddress, Connection> connectionMap; 
    private ConnectionManagerHelper connectionHelper;
    private ConnectionGarbageCollector connectionGarbageCollector;
    private NotificationCenter notificationCenter;
    private ConnectionTimeoutDelegate timeoutDelegate;
    private TimeoutRunner timeoutRunner;
    
    
    IncomingOnlyConnectionManager(Map<SiteAddress, Connection> connectionMap, 
                                  ConnectionManagerHelper outgoingRequestDelegate, 
                                  ConnectionGarbageCollector connectionGarbageCollector, 
                                  NotificationCenter notificationCenter, 
                                  Integer inactiveTimeoutSeconds) {
        this.connectionMap = Collections.synchronizedMap(connectionMap);
        this.connectionHelper = outgoingRequestDelegate;
        this.connectionGarbageCollector = connectionGarbageCollector;
        this.notificationCenter = notificationCenter;
        
        if (inactiveTimeoutSeconds > 0) {
            logger.debug("Starting connection inactivity scanner with timeout = " + inactiveTimeoutSeconds + " seconds.");
            timeoutDelegate = new ConnectionTimeoutDelegate(inactiveTimeoutSeconds, connectionMap);
            this.timeoutRunner = new TimeoutRunner(timeoutDelegate, scanTimeSecondsProperty.currentValue());
            Thread thread = new Thread(this.timeoutRunner, "Connection Timeout Scanner");
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    public IncomingOnlyConnectionManager(ConnectionGarbageCollector connectionGarbageCollector, NotificationCenter notificationCenter, Integer inactiveTimeoutSeconds) {
        this(new HashMap<SiteAddress, Connection>(), new ConnectionManagerHelper(), connectionGarbageCollector, notificationCenter, inactiveTimeoutSeconds);
    }
    
    public void registerForNotification(NotificationCenter notificationCenter) {
        notificationCenter.addObserver(this, NetworkService.NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME);
    }
    
    @Override
    public void manageConnection(Connection connection) {
    	synchronized (connectionMap) {
    		logger.trace("Adding connection for '" + connection.getSiteAddress() + "'. (Connection count = " + connectionMap.size() + ")");
    		connectionMap.put(connection.getSiteAddress(), connection);
    		connection.addConnectionListener(this);
		}
        connectionHelper.queueAnyOutgoingRequestForSite(connection.getSiteAddress());
    }
    
    @Override
    public Connection connectionForSiteAddress(SiteAddress siteAddress) {
        return connectionMap.get(siteAddress);
    }
    
    @Override
    public void removeConnectionWithSiteAddress(NuidSiteAddress siteAddress) {
        Connection connection = connectionMap.get(siteAddress);
        if (connection != null)
            connection.close(); // Rely on callback to remove connections from the connectionMap
    }
    
    @Override
    public Integer currentConnectionCount() {
        return connectionMap.size();
    }

    @Override
    public <T> List<T> collectConnectionInfo(ConnectionInfoCollector<T> collector) {
        List<T> infos = new ArrayList<T>();
        synchronized (connectionMap) {
            for (Connection connection : connectionMap.values())
                infos.add(collector.collectInfo(connection));
        }
        return infos;
    }

    @Override
    public void handleNotification(Notification notification) {
        if (notification.getName().equals(NetworkService.NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME)) {
            List<Connection> connections = new ArrayList<>(connectionMap.values());
            // Rely on callback to remove connections from the connectionMap
            for (Iterator<Connection> iter = connections.iterator(); iter.hasNext(); )
                iter.next().close();
        }
    }
    
    @ConnectionClosed
    void connectionClosed(Connection connection) {
        logger.info("Connection closed");
        removeConnection(connection);
        SiteDisconnectedAttachment attachement = new SiteDisconnectedAttachment(connection.getSiteAddress(), 
                                                                                Constants.BRIDGE_NAME, 
                                                                                connection.getRemoteAddress().getAddress().getHostAddress(),
                                                                                connection.getBytesReceived(),
                                                                                connection.getBytesTransmitted());
        notificationCenter.postNotificationAsync(Connection.CONNECTION_CLOSED_NOTIFICATION, attachement);
    }
    
    @ConnectionIdentityChanged
    void connectionIdentityChanged(Connection connection) {
    	synchronized (connectionMap) {
            // If there is another connection with the same SiteAddress, remove and close it.
            Connection existingConnection = connectionMap.remove(connection.getSiteAddress());
            if (existingConnection != null) {
            	CallCollisionHandler handler = existingConnection.callCollisionHandler();
            	handler.handleCallCollision(connection, existingConnection);
            }
            // The connection could exist in the map, but under a different SiteAddress.  Remove it but don't close it!
            connectionMap.values().remove(connection);
            // And put it in the connection map.
            connectionMap.put(connection.getSiteAddress(), connection);
            connectionHelper.queueAnyOutgoingRequestForSite(connection.getSiteAddress());
		}
        SiteConnectedAttachment attachement = new SiteConnectedAttachment(connection.getSiteAddress(), Constants.BRIDGE_NAME, connection.getRemoteAddress().getHostString(), connection.getFirmwareVersion());
        notificationCenter.postNotificationAsync(Connection.CONNECTION_CONNECTED_NOTIFICATION, attachement);
    }
    
    @ConnectionUnexpectedlyClosed
    void handleConnectionUnexpectedlyClosedEvent(Connection connection, Exception e, SiteAddress siteAddress, SocketAddress address, String message) {
        removeConnection(connection);
        UnexpectedlyClosedAttachement attachement = new UnexpectedlyClosedAttachement(e, siteAddress, address, message);
        notificationCenter.postNotificationAsync(Connection.CONNECTION_LOST_NOTIFICATION, attachement);
    }
    
    private void removeConnection(Connection connection) {
    	connectionMap.remove(connection.getSiteAddress());
    	logger.debug("Connection for '" + connection.getSiteAddress() + "' removed. (Connection count = " + connectionMap.size() + ")");
    	connectionGarbageCollector.queueForCleanup(connection);
    }
    
    
    class TimeoutRunner implements Runnable {
        private ConnectionTimeoutDelegate timeoutDelegate;
        private long timeoutSeconds;
        
        public TimeoutRunner(ConnectionTimeoutDelegate timeoutDelegate, long timeoutSeconds) {
            this.timeoutDelegate = timeoutDelegate;
            this.timeoutSeconds = timeoutSeconds;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(timeoutSeconds));
                } catch (InterruptedException e) {
                }
                timeoutDelegate.run();
            }
        }
    }
}
