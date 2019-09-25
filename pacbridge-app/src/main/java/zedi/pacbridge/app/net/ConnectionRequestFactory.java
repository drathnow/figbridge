package zedi.pacbridge.app.net;

import java.nio.channels.SocketChannel;

import zedi.pacbridge.utl.NotificationCenter;

public class ConnectionRequestFactory {

    public ConnectionRequest newConnectionRequest(Network network, SocketChannel socketChannel, NotificationCenter notificationCenter) {
        return new ConnectionRequest(network, socketChannel, notificationCenter);
    }
}
