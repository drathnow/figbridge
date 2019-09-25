package zedi.pacbridge.utl;


public class DefaultInactivityStrategy implements InactivityStrategy {

    private SystemTime systemTime;
    private Integer timeoutSeconds;
    
    public DefaultInactivityStrategy(Integer inactivityTimeoutSeconds) {
        this.timeoutSeconds = inactivityTimeoutSeconds;
        this.systemTime = new SystemTime();
    }
    
    public void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
    
    @Override
    public boolean isInactiveSinceLastActivityTime(ActivityTracker activityTracker) {
        return systemTime.getCurrentTime() - activityTracker.getLastActivityTime() >= (timeoutSeconds*1000);
    }    

    protected void setInactivityTimeoutSeconds(int inactivityTimeoutSeconds) {
        this.timeoutSeconds = inactivityTimeoutSeconds;
    }

}
