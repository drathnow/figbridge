package zedi.pacbridge.net.core;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.concurrent.DetachedTaskRunner;

class DispatcherThreadPool extends ThreadPoolExecutor implements WorkerPool {
    private static Logger logger = LoggerFactory.getLogger(DispatcherThreadPool.class.getName());

    private static AtomicInteger createdThreadCount = new AtomicInteger(0);
    private DispatcherExecutionListener dispatchTerminationHandler;
    
    public static final String CORE_POOL_SIZE_PROPERTY_NAME = "networkThreadPool.corePoolSize"; 
    public static final int MIN_CORE_POOL_SIZE = 4;
    public static final int DEFAULT_CORE_POOL_SIZE = 4;
    public final static long DEFAULT_TIMEOUT_SECONDS = 10;

    public static final String MAX_POOL_SIZE_PROPERTY_NAME = "networkThreadPool.maxPollSize";
    public static final int MAX_MAX_POOL_SIZE = 20;
    public static final int MIN_MAX_POOL_SIZE = DEFAULT_CORE_POOL_SIZE+1;
    public static final int DEFAULT_MAX_POOL_SIZE = 10;
    
    
    private static IntegerSystemProperty maxPoolSize 
        = new IntegerSystemProperty(MAX_POOL_SIZE_PROPERTY_NAME, DEFAULT_MAX_POOL_SIZE, MIN_MAX_POOL_SIZE, MAX_MAX_POOL_SIZE);
    private static IntegerSystemProperty corePoolSize 
        = new IntegerSystemProperty(CORE_POOL_SIZE_PROPERTY_NAME, DEFAULT_CORE_POOL_SIZE, MIN_CORE_POOL_SIZE);
    
    public DispatcherThreadPool(DispatcherExecutionListener dispatchTerminationHandler) {
        super(corePoolSize.currentValue(), 
                maxPoolSize.currentValue(), 
                DEFAULT_TIMEOUT_SECONDS, 
                TimeUnit.SECONDS, 
                new SynchronousQueue<Runnable>());
        this.dispatchTerminationHandler = dispatchTerminationHandler;
        logger.debug("Network Event Dispatcher thread pool started");
        setThreadFactory(new DispatcherThreadFactory());
        prestartAllCoreThreads();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void afterExecute(Runnable runnable, Throwable exception) {
        DetachedTaskRunner<NetworkEventDispatcher> dispatchRunner = (DetachedTaskRunner<NetworkEventDispatcher>)runnable;
        dispatchTerminationHandler.dispatcherTerminated(dispatchRunner.getTask(), exception);
    }
    
    public void startDispatcher(NetworkEventDispatcher dispatcher) {
        super.execute(new DetachedTaskRunner<NetworkEventDispatcher>(dispatcher));
    }
    
    @Override
    public void execute(Runnable command) {
        throw new UnsupportedOperationException("You cannot call execute directly");
    }
    
    private class DispatcherThreadFactory implements ThreadFactory {
        
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Network Dispatcher - " + createdThreadCount.incrementAndGet());
        }
    }
}
