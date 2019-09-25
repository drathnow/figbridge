package zedi.pacbridge.app.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.DispatcherRequest;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.ThreadContext;

public class ConnectionRequest implements DispatcherRequest {
    public static final String CONNECTION_REQUEST_COMPLETED_NOTIFICATION = ConnectionRequest.class.getName() + ".reqestCompleted";
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionRequest.class.getName());
    
    private ConnectionRequestHandler connectionRequestHandler;
    private SocketChannel socketChannel;
    private NotificationCenter notificationCenter;
    private SystemTime systemTime;
    private long creationTime;

    ConnectionRequest(ConnectionRequestHandler connectionRequestHandler, SocketChannel socketChannel, NotificationCenter notificationCenter, SystemTime systemTime) {
        this.systemTime = systemTime;
        this.connectionRequestHandler = connectionRequestHandler;
        this.socketChannel = socketChannel;
        this.creationTime = systemTime.getCurrentTime();
        this.notificationCenter = notificationCenter;
    }
    
    public ConnectionRequest(ConnectionRequestHandler connectionRequestHandler, SocketChannel socketChannel, NotificationCenter notificationCenter) {
        this(connectionRequestHandler, socketChannel, notificationCenter, SystemTime.SHARED_INSTANCE);
    }

    @Override
    public void handleRequest(DispatcherKey dispatcherKey, ThreadContext astRequester) {
        SocketChannelWrapper channelWrapper = new SocketChannelWrapper(socketChannel);
        try {
            // We register the channel with the dispatcher key here.  From here on, we will not need
            // to do this again.
            dispatcherKey.registerChannel(channelWrapper);
            connectionRequestHandler.handleConnectionRequest(channelWrapper, dispatcherKey, astRequester);
            Long handledTime = systemTime.getCurrentTime() - creationTime;
            notificationCenter.postNotificationAsync(ConnectionRequest.CONNECTION_REQUEST_COMPLETED_NOTIFICATION, handledTime);
        } catch (IOException e) {
            logger.error("Unale to handle connection request", e);
        }
    }
}
