package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

import zedi.pacbridge.net.core.SocketChannelWrapper;

class SocketChannelFactory {

    public SocketChannelWrapper newSocketChannel() throws IOException {
        return new SocketChannelWrapper(SocketChannel.open());
    }
    
    public DatagramChannel newDatagramChannel() throws IOException {
        return DatagramChannel.open();
    }
}
