package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.ChannelFactory;


class ServerHelper {
    private static Logger logger = LoggerFactory.getLogger(ServerHelper.class);

    private Map<String, TcpListenerStatus> listenerStatuses;
    private ChannelFactory socketFactory;
    private Selector selector;
    private boolean notListening;
    private boolean shutdown;

    ServerHelper(Selector selector, ChannelFactory socketFactory) {
        this.selector = selector;
        this.socketFactory = socketFactory;
        this.notListening = true;
        this.listenerStatuses = new HashMap<String, TcpListenerStatus>();
    }

    ListenerStatus registerListener(InetSocketAddress listeningAddress, AcceptHandler acceptHandler, int connectionQueueLimit) throws IOException {
        ServerSocketChannel serverSocketChannel = socketFactory.newServerSocketChannel();
        serverSocketChannel.configureBlocking(false);
        bindAddress(listeningAddress, connectionQueueLimit, serverSocketChannel);
        SocketAddress address = serverSocketChannel.socket().getLocalSocketAddress();
        logger.info("Registering TCP listener on " + address.toString() + " (backLog = " + connectionQueueLimit + ")");
        if (notListening)
            serverSocketChannel.register(selector, 0, acceptHandler);
        else {
            logger.info("Starting TCP listener on address " + address);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, acceptHandler);
        }
        TcpListenerStatus listenerStatus = new TcpListenerStatus();
        listenerStatuses.put(address.toString(), listenerStatus);
        return listenerStatus;
    }

    void stopListening() {
        synchronized (selector.keys()) {
            this.notListening = true;
            for (SelectionKey key : selector.keys()) {
                SocketAddress socketAddress = ((ServerSocketChannel)key.channel()).socket().getLocalSocketAddress();
                logger.info("Stopping TCP listener on address " + socketAddress);
                key.interestOps(0);
            }
        }
    }

    void startListening() {
        synchronized (selector.keys()) {
            this.notListening = false;
            for (SelectionKey key : selector.keys()) {
                SocketAddress socketAddress = ((ServerSocketChannel)key.channel()).socket().getLocalSocketAddress();
                TcpListenerStatus listenerStatus = listenerStatuses.get(socketAddress.toString());
                if (listenerStatus != null)
                    listenerStatus.setListening(true);
                else
                    logger.warn("No listener status could be found for listening address: " + socketAddress.toString());
                logger.info("Starting TCP listener on address " + socketAddress);
                key.interestOps(SelectionKey.OP_ACCEPT);
            }
        }
    }

    void doSelect(long timeoutMilliseconds) throws IOException {
        Set<SelectionKey> selectedKeys;
        selector.select(timeoutMilliseconds);
        synchronized (selectedKeys = selector.selectedKeys()) {
            if (selectedKeys.isEmpty() == false)
                processEventsForSelectedKeys(selectedKeys);
            selectedKeys.clear();
        }
    }

    boolean isShutdown() {
        return shutdown;
    }

    void shutdown() {
        synchronized (selector.keys()) {
            for (SelectionKey key : selector.keys()) {
                try {
                    logger.info("Closing Server Socket for address: " + ((ServerSocketChannel)key.channel()).getLocalAddress()); 
                    ((ServerSocketChannel)key.channel()).close();
                } catch (IOException eatIt) {
                    logger.error("Unable to close socket channel", eatIt);
                }
                key.attach(null);
                key.cancel();
            }
            try {
                selector.close();
            } catch (IOException eatIt) {
            }
        }
        shutdown = true;
    }
    
    private void bindAddress(InetSocketAddress listeningAddress, int connectionQueueLimit, ServerSocketChannel serverSocketChannel) throws IOException, BindException {
        try {
        	ServerSocket socket = serverSocketChannel.socket();
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(listeningAddress, connectionQueueLimit);
        } catch (BindException e) {
            throw new BindException("Address already in use: " + listeningAddress.toString());
        }
    }

    private void processEventsForSelectedKeys(Set<SelectionKey> selectedKeys) {
        for (SelectionKey selectionKey : selectedKeys)
            try {
                SocketChannel socketChannel = ((ServerSocketChannel)selectionKey.channel()).accept();
                logger.info("Connection accepted from " + socketChannel.getRemoteAddress().toString());
                if (selectionKey.isValid() && selectionKey.isAcceptable())
                    ((AcceptHandler)selectionKey.attachment()).handleAcceptForSocketChannel(socketChannel);
            } catch (Exception e) {
                SocketAddress socketAddress = ((ServerSocketChannel)selectionKey.channel()).socket().getLocalSocketAddress();
                logger.error("Unexpected exception encounterd while processing accept event for :" + socketAddress, e);
            }
    }
}
