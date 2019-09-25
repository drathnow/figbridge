package zedi.pacbridge.utl;

/**
 * This is just a simple class to return the current system time.  It can be subclassed
 * to be used in time related tests.
 */
public class SystemTime {
    
    public static final SystemTime SHARED_INSTANCE = new SystemTime();
    
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
    
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
