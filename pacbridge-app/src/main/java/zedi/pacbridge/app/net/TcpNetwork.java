package zedi.pacbridge.app.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;

public class TcpNetwork implements Network {
    private static Logger logger = LoggerFactory.getLogger(TcpNetwork.class.getName());
    
    private Integer number;
    private Integer maxOutgoingRequestSessions;
    private PropertyBag propertyBag;
    private TcpListenerFactory listenerFactory;
    private String typeName;
    private ConnectionManager connectionManager;
    private ConnectionRequestHandler connectionRequestStrategy;
    private boolean started;
    private ListenerStatus listenerStatus;
    
    public TcpNetwork(Integer number, 
                String typeName, 
                TcpListenerFactory listenerFactory, 
                ConnectionRequestHandler connectionRequestStrategy,
                ConnectionManager connectionManager,
                PropertyBag propertyBag) {
        this.number = number;
        this.typeName = typeName;
        this.connectionManager = connectionManager;
        this.connectionRequestStrategy = connectionRequestStrategy;
        this.listenerFactory = listenerFactory;
        this.propertyBag = propertyBag;
        this.started = false;
        setMaxOutgoingRequestSessions(0);
    }

    @Override
    public Integer getNumber() {
        return number;
    }
    
    @Override
    public Integer currentConnectionCount() {
        return connectionManager.currentConnectionCount();
    }
        
    @Override
    public Integer maxOutgoingRequestsSessions() {
        return maxOutgoingRequestSessions;
    }
    
    public void setMaxOutgoingRequestSessions(Integer maxOutgoingRequestSessions) {
        this.maxOutgoingRequestSessions = maxOutgoingRequestSessions;
    }
    
    @Override
    public PropertyBag getPropertyBag() {
        return propertyBag;
    }
    
    @Override
    public String typeName() {
        return typeName;
    }
    
    @Override
    public InetSocketAddress listeningAddress() {
        return listenerFactory.getListeningAddress();
    }
    
    @Override
    public boolean isStarted() {
        return started;
    }
    
    @Override
    public ListenerStatus getListenerStatus() {
        return listenerStatus;
    }
    
    @Override
    public void removeConnectionWithSiteAddress(NuidSiteAddress siteAddress) {
        connectionManager.removeConnectionWithSiteAddress(siteAddress);
    }
    
    @Override
    public void start(ListenerRegistrationAgent registrationAgent, NotificationCenter notificationCenter) {
        if (started == false) {
            started = true;
            try {
                InetSocketAddress address = listenerFactory.getListeningAddress();
                AcceptHandler acceptHandler = listenerFactory.getTcpAcceptHandlerForNetwork(this, notificationCenter);
                listenerStatus = registrationAgent.registerListener(address, acceptHandler, listenerFactory.getConnectionQueueLimit());
            } catch (IOException e) {
                logger.error("Unable to start network (number = " + number + ", type = " + typeName + "')", e);
            }
        }
    }

    @Override
    public void handleConnectionRequest(SocketChannelWrapper socketChannel, DispatcherKey dispatcherKey, ThreadContext astRequester) {
        connectionRequestStrategy.handleConnectionRequest(socketChannel, dispatcherKey, astRequester);
    }

    @Override
    public Connection connectionForSiteAddress(SiteAddress siteAddress) {
        return connectionManager.connectionForSiteAddress(siteAddress);
    }

    @Override
    public <T> List<T> connectionInfo(ConnectionInfoCollector<T> collector) {
        return connectionManager.collectConnectionInfo(collector);
    }
}
