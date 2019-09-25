package zedi.pacbridge.net.core;

import java.nio.channels.SocketChannel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class SocketChannelWrapperMatcher extends BaseMatcher<SocketChannelWrapper>{

    private SocketChannel socketChannel;
    
    public SocketChannelWrapperMatcher(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public boolean matches(Object object) {
        return ((SocketChannelWrapper)object).getChannel().equals(socketChannel);
    }

    @Override
    public void describeTo(Description description) {
    }

}
