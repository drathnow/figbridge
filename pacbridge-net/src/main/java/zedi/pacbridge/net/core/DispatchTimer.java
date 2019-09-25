package zedi.pacbridge.net.core;

import java.util.concurrent.TimeUnit;

import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.utl.Timer;

public class DispatchTimer implements Timer {
    private NetworkEventDispatcher dispatcher;

    public DispatchTimer(NetworkEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public FutureTimer schedule(final Runnable runnable, long delayTime, TimeUnit timeUnit) {
        ThreadContextCommand command = new ThreadContextCommand(new ThreadContextHandler() {
            
            @Override
            public void handleSyncTrap() {
                runnable.run();
            }
        });
        return dispatcher.queueContextCommand(command, delayTime, timeUnit);
    }
}
