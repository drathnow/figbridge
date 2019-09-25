package zedi.pacbridge.test.mocknet;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class MockSelectionKey extends SelectionKey {

    public boolean wasCancelled;
    public Selector selector;
    public int interestOps;
    public int readyOps;
    public boolean valid = true;
    public SelectableChannel channel;
    
    public SelectableChannel channel() {
        return channel;
    }

    public Selector selector() {
        return selector;
    }

    public boolean isValid() {
        return valid;
    }

    public void cancel() {
        wasCancelled = true;
    }

    public int interestOps() {
        return interestOps;
    }
    
    public SelectionKey interestOps(int aOps) {
        interestOps = aOps;
        return this;
    }

    public int readyOps() {
        return readyOps;
    }
}
