package zedi.pacbridge.utl;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class FileChangeMonitor implements Runnable {
    private long lastChangeDate;
    private File monitoredFile;
    private ScheduledFuture<?> future;
    private boolean shutdown;
    private FileChangeHandler changeHandler;
    private ThreadFactory threadFactory;
    private long monitorIntervalSeconds;
    private GlobalScheduledExecutor scheduledExecutor;

    public FileChangeMonitor(GlobalScheduledExecutor scheduledExecutor, File monitoredFile, FileChangeHandler changeHandler, long monitorIntervalSeconds) {
        this(scheduledExecutor, monitoredFile, changeHandler, monitorIntervalSeconds, Executors.defaultThreadFactory());
    }
    
    public FileChangeMonitor(GlobalScheduledExecutor scheduledExecutor, File monitoredFile, FileChangeHandler changeHandler, long monitorIntervalSeconds, ThreadFactory threadFactory) {
        this.changeHandler = changeHandler;
        this.lastChangeDate = monitoredFile.lastModified();
        this.monitoredFile = monitoredFile;
        this.monitorIntervalSeconds = monitorIntervalSeconds;
        this.threadFactory = threadFactory;
        this.scheduledExecutor = scheduledExecutor;
    }
    
    public void start() {
        Thread thread = threadFactory.newThread(this);
        thread.setName("ConfigurationChangeListener");
        thread.start();
    }
    
    @Override
    public void run() {
        if (shutdown == false) {
            if (lastChangeDate != monitoredFile.lastModified()) {
                lastChangeDate = monitoredFile.lastModified();
                changeHandler.fileHasBeenModified(monitoredFile);
            }
            future = scheduledExecutor.schedule(this, monitorIntervalSeconds, TimeUnit.SECONDS);
        }
    }
    
    public void shutdown() {
        shutdown = true;
        if (future != null)
            future.cancel(false);
    }
}
