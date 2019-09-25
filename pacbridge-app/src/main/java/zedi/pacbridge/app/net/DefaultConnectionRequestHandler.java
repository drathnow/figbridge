package zedi.pacbridge.app.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;

public class DefaultConnectionRequestHandler implements ConnectionRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionRequestHandler.class.getName());
    private ThreadLocal<ConnectionBuilder> threadLocal;
    private ConnectionBuilderFactory connectionBuilderFactory;
    private Integer networkNumber;
    private ConnectionManager connectionManager;
    
    DefaultConnectionRequestHandler(ThreadLocal<ConnectionBuilder> threadLocal, 
                                    ConnectionBuilderFactory connectionBuilderFactory, 
                                    Integer networkNumber, 
                                    ConnectionManager connectionManager) {
        this.threadLocal = threadLocal;
        this.connectionBuilderFactory = connectionBuilderFactory;
        this.networkNumber = networkNumber;
        this.connectionManager = connectionManager;
    }

    public DefaultConnectionRequestHandler(ConnectionBuilderFactory connectionBuilderFactory, Integer networkNumber, ConnectionManager connectionManager) {
        this(new ThreadLocal<ConnectionBuilder>(), connectionBuilderFactory, networkNumber, connectionManager);
    }
    
    @Override
    public void handleConnectionRequest(SocketChannelWrapper channelWrapper, DispatcherKey dispatcherKey, ThreadContext astRequester) {
        ConnectionBuilder connectionBuilder = threadLocal.get();
        if (connectionBuilder == null) {
            connectionBuilder = connectionBuilderFactory.newConnectionBuilder();
            threadLocal.set(connectionBuilder);
        }   
        
        String address = ((InetSocketAddress)channelWrapper.socket().getRemoteSocketAddress()).getHostString();
        SiteAddress siteAddress = new IpSiteAddress(address, networkNumber);
        LoggingContext loggingContext = new LoggingContext(siteAddress);
        loggingContext.setupContext();
        try {
            Connection connection = connectionBuilder.newConnection(siteAddress, channelWrapper, dispatcherKey, astRequester);
            connectionManager.manageConnection(connection);
            connection.start();
        } catch (IOException e) {
            logger.error("Unexpected exception while handling connection request.  Call will be dropped", e);
            try {
                channelWrapper.close();
            } catch (IOException eatIt) {
            }
        } finally {
            loggingContext.clearContext();
        }
    }
}