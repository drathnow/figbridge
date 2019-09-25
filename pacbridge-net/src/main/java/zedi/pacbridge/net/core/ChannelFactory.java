package zedi.pacbridge.net.core;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class ChannelFactory {

    public ServerSocketChannel newServerSocketChannel() throws IOException {
        return SelectorProvider.provider().openServerSocketChannel();
    }

    public DatagramChannel newDatagramChannel() throws IOException {
        return SelectorProvider.provider().openDatagramChannel();
    }

}
