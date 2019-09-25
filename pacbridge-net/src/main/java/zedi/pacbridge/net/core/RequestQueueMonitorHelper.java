package zedi.pacbridge.net.core;

public interface RequestQueueMonitorHelper {

    public void createNewWorker();
    public int getCoreWorkerCount();
    public int getCurrentWorkerCount();
    public long getScanTimeMilliseconds();
    public int getCriticalQueueDepthThreshold();
    public int getQueueDepthHysteresis();
    public long getQueueDepthSanityTimeMilliseconds();
}