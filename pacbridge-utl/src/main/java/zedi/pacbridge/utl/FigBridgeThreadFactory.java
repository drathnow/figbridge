package zedi.pacbridge.utl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Stateless;

@Stateless(name=FigBridgeThreadFactory.JNDI_NAME)
public class FigBridgeThreadFactory implements ThreadFactory {
    public static final String JNDI_NAME = "java:global/ThreadFactory";
    
    static AtomicInteger index = new AtomicInteger(0);
    static ThreadGroup pacBridgeThreadGroup = new ThreadGroup("FigBridge");

    public Thread newThread(Runnable runnable) {
        String name = "figbridge-thread-" + index.incrementAndGet();
        return newThread(runnable, name);
    }

    public Thread newThread(Runnable runnable, String name) {
        Thread thread = new Thread(pacBridgeThreadGroup, runnable, name);
        thread.setDaemon(true);
        return thread;
    }
}
