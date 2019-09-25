package zedi.pacbridge.net.core;

import zedi.pacbridge.utl.ThreadContextHandler;

public class ThreadContextCommand implements ContextCommand {

    private ThreadContextHandler handler;
    
    public ThreadContextCommand(ThreadContextHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute() {
        handler.handleSyncTrap();
    }
}
