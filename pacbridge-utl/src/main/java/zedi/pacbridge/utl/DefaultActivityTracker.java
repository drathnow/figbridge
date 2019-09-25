package zedi.pacbridge.utl;

public class DefaultActivityTracker implements ActivityTracker {

    private long lastActivity;
    
    public DefaultActivityTracker() {
        this.lastActivity = System.currentTimeMillis();
    }
    
    @Override
    public void update() {
        lastActivity = System.currentTimeMillis();
    }

    @Override
    public long getLastActivityTime() {
        return lastActivity;
    }

}
