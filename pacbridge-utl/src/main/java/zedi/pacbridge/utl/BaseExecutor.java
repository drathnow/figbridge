package zedi.pacbridge.utl;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class BaseExecutor {

    protected ThreadPoolExecutor threadPoolExecutor;
    protected long startTime;

    protected BaseExecutor() {
    }
    
    protected BaseExecutor(ThreadPoolExecutor threadPoolExecutor) {
        startTime = System.currentTimeMillis();
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public boolean isTerminated() {
        return threadPoolExecutor.isTerminated();
    }
    
    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }
    
    public void setCorePoolSize(int size) {
        threadPoolExecutor.setCorePoolSize(size);
    }
    
    public int getCorePoolSize() {
        return threadPoolExecutor.getCorePoolSize();
    }
    
    public int getCurrentPollSize() {
        return threadPoolExecutor.getPoolSize();
    }
    
    public void setMaxPoolSize(int size) {
        threadPoolExecutor.setMaximumPoolSize(size);
    }
    
    public int getMaxPoolSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }
    
    public int getLargestPoolSize() {
        return threadPoolExecutor.getLargestPoolSize();
    }
    
    public void setKeepAliveTime(long seconds) {
        threadPoolExecutor.setKeepAliveTime(seconds, TimeUnit.SECONDS);
    }
    
    public long getKeepAliveTime() {
        return threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);
    }
    
    public int getQueuedTaskCount() {
        return threadPoolExecutor.getQueue().size();
    }
    
    public long getCompletedTaskCount() {
        return threadPoolExecutor.getCompletedTaskCount();
    }
    
    public Date getStartTime() {
        return new Date(startTime);
    }

    public boolean isTerminating() {
        return threadPoolExecutor.isTerminating();
    }

    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    public void shutdownNow() {
        threadPoolExecutor.shutdownNow();
    }
}
