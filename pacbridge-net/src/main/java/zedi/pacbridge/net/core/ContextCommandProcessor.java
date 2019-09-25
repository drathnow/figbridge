package zedi.pacbridge.net.core;

import java.util.concurrent.TimeUnit;

import zedi.pacbridge.utl.FutureTimer;

public interface ContextCommandProcessor {
    public void queueContextCommand(ContextCommand contextCommand);
    public FutureTimer queueContextCommand(ContextCommand contextCommand, long delayTime, TimeUnit timeUnit);
}
