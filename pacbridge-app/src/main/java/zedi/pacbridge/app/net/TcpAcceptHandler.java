package zedi.pacbridge.app.net;

import java.nio.channels.SocketChannel;

import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.NotificationCenter;

public class TcpAcceptHandler implements AcceptHandler {

    private Network network;
    private ConnectionRequestFactory connectionRequestFactory;
    private NotificationCenter notificationCenter;
    private NetworkEventDispatcherManager dispatcherManager;
    
    TcpAcceptHandler(Network network, NotificationCenter notificationCenter, ConnectionRequestFactory connectionRequestFactory, NetworkEventDispatcherManager dispatcherManager) {
        this.network = network;
        this.connectionRequestFactory = connectionRequestFactory;
        this.notificationCenter = notificationCenter;
        this.dispatcherManager = dispatcherManager;
    }
    
    public TcpAcceptHandler(Network network, NotificationCenter notificationCenter, NetworkEventDispatcherManager dispatcherManager) {
        this(network, notificationCenter, new ConnectionRequestFactory(), dispatcherManager);
    }

    @Override
    public void handleAcceptForSocketChannel(SocketChannel socketChannel) {
        ConnectionRequest connectionRequest = connectionRequestFactory.newConnectionRequest(network, socketChannel, notificationCenter);
        dispatcherManager.queueDispatcherRequest(connectionRequest);
    }
}