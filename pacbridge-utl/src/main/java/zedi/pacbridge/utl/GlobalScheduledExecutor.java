package zedi.pacbridge.utl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GlobalScheduledExecutor {
    private static Logger logger = LoggerFactory.getLogger(GlobalScheduledExecutor.class);
    public static final String THREAD_COUNT_PROPERTY_NAME = "globalScheduledExcecutor.threadCount";
    public static final int DEFAULT_THREAD_COUNT = 4;

    public static final IntegerSystemProperty initialThreadCount = new IntegerSystemProperty(THREAD_COUNT_PROPERTY_NAME, DEFAULT_THREAD_COUNT);

    static int defaultThreadCount;

    ThreadPoolExecutor threadPoolExecutor;

    public static void setDefaultThreadCount(int threadCount) {
        defaultThreadCount = threadCount;
    }

    public GlobalScheduledExecutor() {
        if (defaultThreadCount == 0)
            defaultThreadCount = initialThreadCount.currentValue();
        threadPoolExecutor = new ScheduledThreadPoolExecutor(defaultThreadCount);
        logger.debug("GlobalScheduledExecutor initialized with " + defaultThreadCount + " threads.");
    }

    public int getCurrentNumberOfScheduledTasks() {
        return threadPoolExecutor.getQueue().size();
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delaySeconds) {
        return schedule(runnable, delaySeconds, TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnits) {
        return ((ScheduledThreadPoolExecutor)threadPoolExecutor).schedule(runnable, delay, timeUnits);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delaySeconds, long periodSeconds, TimeUnit units) {
        return ((ScheduledThreadPoolExecutor)threadPoolExecutor).scheduleAtFixedRate(runnable, delaySeconds, periodSeconds, units);
    }

    public void remove(Runnable runnable) {
        ((ScheduledThreadPoolExecutor)threadPoolExecutor).remove(runnable);
    }

    public void shutdown() {
        ((ScheduledThreadPoolExecutor)threadPoolExecutor).shutdown();
    }
    
    public int getQueueLength() {
        return threadPoolExecutor.getQueue().size();
    }

    public void clear() {
        threadPoolExecutor.getQueue().clear();
    }
}
