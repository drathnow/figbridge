package zedi.pacbridge.net.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public class SocketChannelWrapper implements ChannelWrapper {

    private SocketChannel socketChannel;

    public SocketChannelWrapper(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
    
    @Override
    public void configureBlocking(boolean shouldBlock) throws IOException {
        socketChannel.configureBlocking(shouldBlock);
    }
    
    public void finishConnect() throws IOException {
        socketChannel.finishConnect();
    }

    public void connect(InetSocketAddress remoteAddress) throws IOException {
        socketChannel.connect(remoteAddress);
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
    }

    public InetSocketAddress remoteAddress() throws IOException {
        return (InetSocketAddress)socketChannel.getRemoteAddress();
    }
    
    public int write(ByteBuffer byteBuffer) throws IOException {
        return socketChannel.write(byteBuffer);
    }

    public boolean isSocketClosed() {
        return socketChannel.socket().isClosed();
    }

    public int read(ByteBuffer receiveByteBuffer) throws IOException {
        return socketChannel.read(receiveByteBuffer);
    }

    public void shutdownOutputOnSocket() throws IOException {
        socketChannel.socket().shutdownOutput();
    }
    
    public Socket socket() {
        return socketChannel.socket();
    }

    @Override
    public SelectableChannel getChannel() {
        return socketChannel;
    }
}
