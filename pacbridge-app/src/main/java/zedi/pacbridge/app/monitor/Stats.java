package zedi.pacbridge.app.monitor;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

class Stats {
    private static TimeZone timeZone = TimeZone.getDefault();
    private static long rawOffset = timeZone.getRawOffset();
    
    public static int currentMinuteOfDay() {
        long value = System.currentTimeMillis() + rawOffset + timeZone.getDSTSavings();
        return (int)TimeUnit.MILLISECONDS.toMinutes(value) % 1440; 
    }
}
