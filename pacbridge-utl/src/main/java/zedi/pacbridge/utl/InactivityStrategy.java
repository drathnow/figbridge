package zedi.pacbridge.utl;



public interface InactivityStrategy {
    public boolean isInactiveSinceLastActivityTime(ActivityTracker activityTracker);
}
