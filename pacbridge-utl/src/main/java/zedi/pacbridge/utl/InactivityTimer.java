package zedi.pacbridge.utl;

import java.util.concurrent.TimeUnit;

public class InactivityTimer {
    private long inactivityMilliseconds;
    private long startTime;
    private SystemTime systemTime;

    public InactivityTimer(long inactivityMilliseconds, SystemTime systemTime) {
        this.inactivityMilliseconds = inactivityMilliseconds;
        this.systemTime = systemTime;
    }

    public InactivityTimer(long inactivityMilliseconds) {
        this(inactivityMilliseconds, new SystemTime());
    }
    
    public InactivityTimer(long inactivityTime, TimeUnit timeUnit) {
        this.inactivityMilliseconds = timeUnit.toMillis(inactivityTime);
    }
    
    public void start() {
        this.startTime = systemTime.getCurrentTime();
    }
    
    public boolean isExpired() {
        long diff = systemTime.getCurrentTime() - startTime; 
        return diff > inactivityMilliseconds;
    }
}
