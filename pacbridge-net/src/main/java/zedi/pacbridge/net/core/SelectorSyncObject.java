package zedi.pacbridge.net.core;

import java.nio.channels.Selector;

public class SelectorSyncObject implements SynchObject {

    private Selector selector;

    public SelectorSyncObject(Selector selector) {
        this.selector = selector;
    }
    
    @Override
    public void notifyListener() {
        selector.wakeup();
    }
}
