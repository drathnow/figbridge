package zedi.pacbridge.app.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ConnectEventHandler;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.ThreadContext;

/**
 * Class used to connect to remote devices.
 */
public class RemoteDeviceConnector implements ConnectEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDeviceConnector.class.getName());

    private LoggingContext loggingContext;
    private SocketChannelWrapper socketChannel;
    private DispatcherKey dispatcherKey;
    private ThreadContext threadContext;
    private RemoteDeviceConnectorListener listener;
    
    public RemoteDeviceConnector(LoggingContext loggingContext, 
                                 Integer portNumber, 
                                 SocketChannelWrapper socketChannel, 
                                 DispatcherKey dispatcherKey, 
                                 ThreadContext threadContext, 
                                 RemoteDeviceConnectorListener listener) throws IOException {
        this.loggingContext = loggingContext;
        this.socketChannel = socketChannel;
        this.dispatcherKey = dispatcherKey;
        this.threadContext = threadContext;
        this.listener = listener;

        this.socketChannel.configureBlocking(false);
        this.dispatcherKey.registerChannel(socketChannel);
        this.dispatcherKey.addChannelInterest(socketChannel, SelectionKey.OP_CONNECT);
        InetSocketAddress socketAddress = new InetSocketAddress(loggingContext.siteAddress().getAddress(), portNumber);
        this.socketChannel.connect(socketAddress);
    }

    @Override
    public LoggingContext loggingContext() {
        return loggingContext;
    }
    
    @Override
    public void handleConnect() {
        try {
            socketChannel.finishConnect();
            listener.connected(loggingContext, socketChannel, dispatcherKey, threadContext);
        } catch (IOException e) {
            logger.error("Unable to finish connect", e);
        }
    }
}