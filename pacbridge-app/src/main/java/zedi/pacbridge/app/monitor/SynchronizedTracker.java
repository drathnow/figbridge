package zedi.pacbridge.app.monitor;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class SynchronizedTracker implements Serializable {
    public static final Integer MINUTES_IN_A_DAY = 1440;
    private Lock lock;
    
    protected SynchronizedTracker() {
        this.lock = new ReentrantLock();
    }
    
    protected void lock() {
        lock.lock();
    }
    
    protected void unlock() {
        lock.unlock();
    }
}
