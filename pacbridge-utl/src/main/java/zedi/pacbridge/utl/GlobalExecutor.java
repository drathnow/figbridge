package zedi.pacbridge.utl;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GlobalExecutor extends BaseExecutor implements Executor {
    
	public static final String INITIAL_THREAD_COUNT_PROPERTY_NAME = "globalThreadPool.coreThreadCount";
	public static final String THREAD_TIMEOUT_SECONDS_PROPERTY_NAME = "globalThreadPool.threadIdleTimeoutSeconds";
	public static final int DEFAULT_CORE_THREAD_COUNT = 20;
	public static final long DEFAULT_THREAD_TIMEOUT_SECONDS = 5L;
    protected static IntegerSystemProperty initialThreadCount = new IntegerSystemProperty(INITIAL_THREAD_COUNT_PROPERTY_NAME, DEFAULT_CORE_THREAD_COUNT);
    protected static IntegerSystemProperty threadTimeout = new IntegerSystemProperty(THREAD_TIMEOUT_SECONDS_PROPERTY_NAME, DEFAULT_THREAD_TIMEOUT_SECONDS);
	
	protected static GlobalExecutor sharedInstance;
	
	public static GlobalExecutor sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new GlobalExecutor();
		return sharedInstance;
	}
	
	public GlobalExecutor() {
		super(new ThreadPoolExecutor(initialThreadCount.currentValue()
		                              , initialThreadCount.currentValue() // has no effect since we are using a LinkedBlockingQueue
		                              , threadTimeout.currentValue()
		                              , TimeUnit.SECONDS
		                              , new LinkedBlockingQueue<Runnable>()));
	}
}
