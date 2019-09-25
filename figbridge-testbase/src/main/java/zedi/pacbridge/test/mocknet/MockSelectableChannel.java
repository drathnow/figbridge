package zedi.pacbridge.test.mocknet;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

public class MockSelectableChannel extends SelectableChannel{

    public boolean closeCalled; 
    
    @Override
    public SelectorProvider provider() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public int validOps() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public boolean isRegistered() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public SelectionKey keyFor(Selector sel) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public SelectionKey register(Selector sel, int ops, Object att) throws ClosedChannelException {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public SelectableChannel configureBlocking(boolean block) throws IOException {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public boolean isBlocking() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Object blockingLock() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    protected void implCloseChannel() throws IOException {
        closeCalled = true;
    }

}
