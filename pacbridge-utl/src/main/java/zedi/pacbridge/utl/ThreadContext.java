package zedi.pacbridge.utl;

import java.util.concurrent.TimeUnit;



public interface ThreadContext {
    public boolean isCurrentContext();
    public void requestTrap(ThreadContextHandler handler);
    public FutureTimer requestTrap(ThreadContextHandler handler, Long delay, TimeUnit timeUnit);
    public Timer getTimer();
}
