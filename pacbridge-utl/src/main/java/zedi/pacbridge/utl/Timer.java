package zedi.pacbridge.utl;

import java.util.concurrent.TimeUnit;

public interface Timer {
    public FutureTimer schedule(Runnable runnable, long delay, TimeUnit timeUnit);
}