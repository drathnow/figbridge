package zedi.pacbridge.app.net;

import java.net.InetSocketAddress;

import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.NotificationCenter;

class TcpListenerFactory {
    private String listeningAddress;
    private Integer listeningPort;
    private Integer connectionQueueLimit;
    private NetworkEventDispatcherManager dispatchManager;

    public TcpListenerFactory(String listeningAddress, Integer listeningPort, Integer connectionQueueLimit, NetworkEventDispatcherManager dispatchManager) {
        this.listeningAddress = listeningAddress;
        this.listeningPort = listeningPort;
        this.connectionQueueLimit = connectionQueueLimit;
        this.dispatchManager = dispatchManager;
    }

    public Integer getConnectionQueueLimit() {
        return connectionQueueLimit;
    }
    
    public InetSocketAddress getListeningAddress() {
        return listeningAddress == null ? new InetSocketAddress(listeningPort) : new InetSocketAddress(listeningAddress, listeningPort);
    }
    
    public AcceptHandler getTcpAcceptHandlerForNetwork(Network network, NotificationCenter notificationCenter) {
        return new TcpAcceptHandler(network, notificationCenter, dispatchManager);
    }
}
