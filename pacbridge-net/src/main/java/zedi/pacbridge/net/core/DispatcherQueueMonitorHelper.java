package zedi.pacbridge.net.core;

import zedi.pacbridge.utl.IntegerSystemProperty;

class DispatcherQueueMonitorHelper implements RequestQueueMonitorHelper {

    // Duration of time between each check of the queue depth
    public static final String SCAN_TIME_PROPERTY_NAME = "pacbridge.dispatcherQueueMonitor.scanTimeMilliseconds";
    public static final int DEFAULT_SCANT_TIME_MILLISECONDS = 500;
    
    // When above this threshold, action is to be taken.
    public static final String CRITICAL_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME = "pacbridge.dispatcherQueueMonitor.criticalRequestQueueDepthThreshold";
    public static final int DEFAULT_CRITICAL_QUEUE_DEPTH_THRESHOLD = 10;

    //  Must stay above threshold for this time before action is taken 
    public static final String QUEUE_DEPTH_SANITY_TIME_PROPERTY_NAME = "pacbridge.dispatcherQueueMonitor.queueDepthSanityTimeMilliseconds";
    public static final int DEFAULT_QUEUE_DEPTH_SANITY_TIME_MILLISECONDS = 2000;

    // Once we start adding dispatchers, when queue depth drop below this value, 
    // we stop adding new dispatchers.
    public static final String QUEUE_DEPTH_HYSTERISIS_PROPERTY_NAME = "pacbridge.dispatcherQueueMonitor.queueDepthHysteresis";
    public static final int DEFAULT_QUEUE_DEPTH_HYSTERISIS = 7;
    
    private int criticalQueueDepthThreshold = IntegerSystemProperty.valueOf(CRITICAL_QUEUE_DEPTH_THRESHOLD_PROPERTY_NAME, DEFAULT_CRITICAL_QUEUE_DEPTH_THRESHOLD);
    private int queueDepthSanityTimeMilliseconds = IntegerSystemProperty.valueOf(QUEUE_DEPTH_SANITY_TIME_PROPERTY_NAME, DEFAULT_QUEUE_DEPTH_SANITY_TIME_MILLISECONDS);
    private int queueDepthHysteresis = IntegerSystemProperty.valueOf(QUEUE_DEPTH_HYSTERISIS_PROPERTY_NAME, DEFAULT_QUEUE_DEPTH_HYSTERISIS);
    private long scanTimeMilliseconds = IntegerSystemProperty.valueOf(SCAN_TIME_PROPERTY_NAME, DEFAULT_SCANT_TIME_MILLISECONDS);
 
    private DispatcherManager dispatcherManager;
    private WorkerPool workerPool;
    
    public DispatcherQueueMonitorHelper(DispatcherManager dispatcherManager, WorkerPool workerPool) {
        this.dispatcherManager = dispatcherManager;
        this.workerPool = workerPool;
    }
    
    public void createNewWorker() {
        dispatcherManager.startNewDispatcher();
    }

    public int getCoreWorkerCount() {
        return workerPool.getCorePoolSize();
    }

    public int getCurrentWorkerCount() {
        return workerPool.getPoolSize();
    }

    public long getScanTimeMilliseconds() {
        return scanTimeMilliseconds;
    }
    
    public int getCriticalQueueDepthThreshold() {
        return criticalQueueDepthThreshold;
    }
    
    public int getQueueDepthHysteresis() {
        return queueDepthHysteresis;
    }
    
    public long getQueueDepthSanityTimeMilliseconds() {
        return queueDepthSanityTimeMilliseconds;
    }
}
