package zedi.pacbridge.net.core;

import java.util.concurrent.TimeUnit;

import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.utl.Timer;

public class NetworkEventDispatcherThreadContext implements ThreadContext {

    private NetworkEventDispatcher dispatcher;
    private Timer timer;
    
    public NetworkEventDispatcherThreadContext(NetworkEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean isCurrentContext() {
        return dispatcher.isCurrentContext();
    }
    
    @Override
    public void requestTrap(ThreadContextHandler handler) {
        ThreadContextCommand command = new ThreadContextCommand(handler);
        dispatcher.queueContextCommand(command);
    }

    @Override
    public FutureTimer requestTrap(ThreadContextHandler handler, Long delay, TimeUnit timeUnit) {
        ThreadContextCommand command = new ThreadContextCommand(handler);
        return dispatcher.queueContextCommand(command, delay, timeUnit);        
    }

    @Override
    public Timer getTimer() {
        if (timer == null)
            timer = new DispatchTimer(dispatcher);
        return timer;
    }
}
