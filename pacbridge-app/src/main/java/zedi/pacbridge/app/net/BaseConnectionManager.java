package zedi.pacbridge.app.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public abstract class BaseConnectionManager {
    protected static ScheduledExecutorService schedulingService = 
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                int cnt = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "connection-manager-thread-" + cnt++);
                    thread.setDaemon(true);
                    return thread;
                }
            });
    
    protected ScheduledExecutorService schedulingService() {
        return schedulingService;
    }
}
