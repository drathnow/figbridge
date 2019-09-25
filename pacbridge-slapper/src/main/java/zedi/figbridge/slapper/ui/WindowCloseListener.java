package zedi.figbridge.slapper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.inject.Inject;

import zedi.figbridge.slapper.BridgeSlapper;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;

public class WindowCloseListener extends WindowAdapter {
    private NetworkEventDispatcherManager manager;
    private BridgeSlapper bridgeSlapper;
    
    public WindowCloseListener() {
    }
    
    @Inject
    public WindowCloseListener(BridgeSlapper bridgeSlapper, NetworkEventDispatcherManager manager) {
        this.bridgeSlapper = bridgeSlapper;
        this.manager = manager;
    }
    
    public void windowClosing(WindowEvent e) {
        bridgeSlapper.stop();
        manager.shutdown();
    }
}
