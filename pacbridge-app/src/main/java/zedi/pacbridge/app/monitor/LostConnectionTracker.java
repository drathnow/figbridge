package zedi.pacbridge.app.monitor;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.hibernate.search.annotations.Field;

import zedi.pacbridge.utl.SiteAddress;


/**
 * The LostConnectionTracker tracks lost connection events for a specific site.
 * 
 */
public class LostConnectionTracker implements Serializable {
    private static final long MillisInAMinute = 60000L;

    @Field
    private String nuid;
    @Field
    private Long lastLostConnectionTime;
    @Field
    private Map<Long, String> exceptions;
    @Field
    private boolean inAlarm;
    @Field
    private int lostConnectionThesholdMinutes;
    @Field
    private int lostConnectionThesholdCount;
    @Field
    private boolean shouldRaiseAlarm;

    LostConnectionTracker(SiteAddress siteAddress, Integer lostConnectionThesholdCount, Integer lostConnectionThesholdMinutes, Map<Long, String> exceptions) {
        this.nuid = siteAddress.getAddress();
        this.lostConnectionThesholdCount = lostConnectionThesholdCount;
        this.lostConnectionThesholdMinutes = lostConnectionThesholdMinutes;
        this.exceptions = exceptions;
        this.lastLostConnectionTime = 0L;
        this.inAlarm = false;
        this.shouldRaiseAlarm = false;
    }

    public LostConnectionTracker(SiteAddress siteAddress, Integer lostConnectionThesholdCount, Integer lostConnectionThesholdMinutes) {
        this(siteAddress, lostConnectionThesholdCount, lostConnectionThesholdMinutes,  new TreeMap<Long, String>());
    }   

    public String siteAddress() {
        return nuid;
    }

    public boolean shouldRaiseAlarm() {
        return shouldRaiseAlarm;
    }
    
    public void recordLostConnection(Exception exception, Long currentTime) {
        if (TimeUnit.MILLISECONDS.toMinutes(currentTime - lastLostConnectionTime) > lostConnectionThesholdMinutes) {
            lastLostConnectionTime = currentTime;
            exceptions.clear();
            exceptions.put(currentTime, exception.toString());
            inAlarm = false;
        } else {
            exceptions.put(currentTime, exception.toString());
            if (exceptions.size() > lostConnectionThesholdCount) {
                inAlarm = true;
                shouldRaiseAlarm = true;
            }
        }
    }

    boolean isInAlarm() {
        return inAlarm;
    }
}
