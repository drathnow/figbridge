package zedi.pacbridge.app.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.net.NetworkAdapterListener;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.net.auth.AuthenticationContext;
import zedi.pacbridge.net.auth.AuthenticationListener;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;


public class DeviceConnection implements Connection, Comparable<Connection> {
    private static final Logger logger = LoggerFactory.getLogger(DeviceConnection.class.getName());
    
    private static final Long authenticationTimeoutSeconds = (long)IntegerSystemProperty.currentValue(AUTHENTICATION_TIMEOUT_PROPERTY_NAME, 
                                                                                                   DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS,
                                                                                                   MIN_AUTHENTICATION_TIMEOUT_SECONDS,
                                                                                                   MAX_AUTHENTICATION_TIMEOUT_SECONDS);
 
    private SiteAddress siteAddress;
    private List<ConnectionListenerEventAdapter> eventAdapters;
    private ThreadContext threadContext;
    private OutgoingRequestSession outgoingRequestSession;
    private ProtocolStack protocolStack;
    private boolean closed;
    private FutureTimer future;
    private String firmwareVersion;
    
    public DeviceConnection(SiteAddress siteAddress, 
                            ThreadContext threadContext, 
                            ProtocolStack protocolStack, 
                            GlobalScheduledExecutor scheduledExecutor) {
        this.siteAddress = siteAddress;
        this.threadContext = threadContext;
        this.protocolStack = protocolStack;
        this.eventAdapters = Collections.synchronizedList(new ArrayList<ConnectionListenerEventAdapter>());
        protocolStack.setAuthenticationListener(new AuthenticationListener() {

            @Override
            public void deviceAuthenticated(AuthenticationContext authenticationContext) {
                logger.info("Connection authenticated as " + authenticationContext.getSiteAddress().getAddress());
                setSiteAddress(authenticationContext.getSiteAddress());
                DeviceConnection.this.firmwareVersion = authenticationContext.getFirmwareVersion();
                for (ConnectionListenerEventAdapter eventAdapter : eventAdapters)
                    eventAdapter.postIdentityChangedEvent();
                if (future != null)
                    future.cancel();
            }

            @Override
            public void authenticationStarted() {
                ThreadContextHandler handler = new ThreadContextHandler() {
                    @Override
                    public void handleSyncTrap() {
                        LoggingContext context = new LoggingContext(siteAddress);
                        context.setupContext();
                        logger.warn("Device failed to authenticate before authentication timeout expired. Connection closed");
                        context.clearContext();
                        close();
                    }
                };
                
                //
                // Once the authentication process starts, we will give the Connection a certain amount of 
                // time to authenticate.  If the timer expires, we will close the connection
                //
                if (future != null)
                    future.cancel();
                future = threadContext.requestTrap(handler, authenticationTimeoutSeconds, TimeUnit.SECONDS);
            }
            
            @Override
            public void authenticationFailed() {
                if (future != null)
                    future.cancel();
                future = null;
                close();
            }
            
        });
        protocolStack.setNetworkAdapterListener(new NetworkAdapterListener(){

            @Override
            public void handleCloseEvent() {
                closeThisConnection();
            }

            @Override
            public void handleUnexpectedCloseEvent(Exception exception, SiteAddress siteAddress, SocketAddress address, String message) {
                for (ConnectionListenerEventAdapter eventAdapter : eventAdapters)
                    eventAdapter.postUnexpectedCloseEvent(exception, siteAddress, address, message);
                if (future != null)
                    future.cancel();
                future = null;
            }
            
        });
    }
    
    public Integer getMaxSessionLimit() {
        return 0;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    public void setSiteAddress(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
        protocolStack.setSiteAddress(siteAddress);
    }
    
    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return protocolStack.getRemoteAddress();
    }
    
    @Override
    public int getBytesReceived() {
        return protocolStack.getBytesReceived();
    }
    
    @Override
    public int getBytesTransmitted() {
        return protocolStack.getBytesTransmitted();
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public Long getLastActivityTime() {
        return protocolStack.getLastActivityTime();
    }
    
    public void addConnectionListener(Object listener) {
        this.eventAdapters.add(new ConnectionListenerEventAdapter(this, listener));
    }
    
    public OutgoingRequestSession outgoingRequestSessionForOutgoingRequest(OutgoingRequest outgoingRequest) {
        return new OutgoingRequestSession(outgoingRequest, this, threadContext);
    }

    @Override
    public CallCollisionHandler callCollisionHandler() {
    	return new DeviceConnectionCallCollisionHandler();
    }

    public void close() {
        if (threadContext.isCurrentContext())
            closeThisConnection();
        else
            threadContext.requestTrap(new ThreadContextHandler() {
                @Override
                public void handleSyncTrap() {
                    closeThisConnection();
                }
            });
    }

    @Override
    public int compareTo(Connection otherConnection) {
        return siteAddress.compareTo(otherConnection.getSiteAddress());
    }
    
    public boolean isClosed() {
        return closed;
    }
    
    public boolean isActive() {
        return outgoingRequestSession != null;
    }
    
    private void closeThisConnection() {
        if (future != null)
            future.cancel();
        future = null;
        closed = true;
        protocolStack.close();
        if (outgoingRequestSession != null)
            outgoingRequestSession.close();
        outgoingRequestSession = null;
        postCloseEvent();
    }

    public Session newSession() {
        return protocolStack.newSession();
    }

    @Override
    public void start() throws IOException {
        protocolStack.start();
    }
    
    private void postCloseEvent() {
        for (ConnectionListenerEventAdapter eventAdapter : eventAdapters)
            eventAdapter.postClosedEvent();
        eventAdapters.clear();
    }
}