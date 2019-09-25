package zedi.pacbridge.net.core;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.IntegerSystemProperty;

public class EventWorkerThreadPool extends ThreadPoolExecutor implements WorkerPool {

    public static final int KEEP_ALIVE_SECONDS = 10;
    
    public static final String GROWTH_INCREMENT_PROPERTY_NAME = "network.eventWorkerPool.growthIncrementSize";
    public static final int DEFAULT_GROWTH_INCREMENT = 5;
    public static final int MIN_GROWTH_INCREMENT = 1;

    public final static String CORE_POOL_SIZE_PROPERTY_NAME = "network.eventWokerPool.corePoolSize";
    public static final int DEFAULT_CORE_POOL_SIZE = 5;
    public static final int MIN_CORE_POOL_SIZE = 1;
    public static final int MAX_CORE_POOL_SIZE = 20;
    
    public final static String MAX_POOL_SIZE_PROPERTY_NAME = "network.eventWokerPool.maxPoolSize";
    public static final int DEFAULT_MAX_POOL_SIZE = 5;
    public static final int MIN_MAX_POOL_SIZE = 1;
    public static final int MAX_MAX_POOL_SIZE = 20;
    
    private static IntegerSystemProperty growthIncrement 
            = new IntegerSystemProperty(GROWTH_INCREMENT_PROPERTY_NAME, DEFAULT_GROWTH_INCREMENT, MIN_GROWTH_INCREMENT); 
    private static IntegerSystemProperty corePoolSize 
            = new IntegerSystemProperty(CORE_POOL_SIZE_PROPERTY_NAME, DEFAULT_CORE_POOL_SIZE, MIN_CORE_POOL_SIZE, MAX_CORE_POOL_SIZE); 
    private static IntegerSystemProperty maxPoolSize 
            = new IntegerSystemProperty(MAX_POOL_SIZE_PROPERTY_NAME, DEFAULT_MAX_POOL_SIZE, MIN_MAX_POOL_SIZE, MAX_MAX_POOL_SIZE); 
    
    private static Logger logger = LoggerFactory.getLogger(EventWorkerThreadPool.class);
    
    private int createdThreadCount = 0; 
    
    public EventWorkerThreadPool() {
        super(corePoolSize.currentValue(), 
                maxPoolSize.currentValue(), 
                KEEP_ALIVE_SECONDS, 
                TimeUnit.SECONDS, 
                new SynchronousQueue<Runnable>());
        setThreadFactory(new EventRunnerThreadFactory());
    }
    
    @Override
    public void execute(Runnable command) {
        if (getPoolSize() == getMaximumPoolSize()) {
            int newMaxPoolSize = getMaximumPoolSize() + growthIncrement.currentValue();
            logger.warn("Expanding event work pool from " + getPoolSize() + " to " + newMaxPoolSize);
            setMaximumPoolSize(newMaxPoolSize);
        }
        super.execute(command);
    }
    
    private class EventRunnerThreadFactory implements ThreadFactory {
        
        @Override
        public Thread newThread(Runnable runnable) {
            createdThreadCount++;
            return new Thread(runnable, "Event Worker - " + createdThreadCount);
        }
    }
}
