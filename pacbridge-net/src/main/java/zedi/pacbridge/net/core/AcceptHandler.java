package zedi.pacbridge.net.core;

import java.nio.channels.SocketChannel;

public interface AcceptHandler {
    public void handleAcceptForSocketChannel(SocketChannel socketChannel);
}
