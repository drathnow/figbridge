package zedi.pacbridge.app.monitor;

import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.infinispan.Cache;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.SiteConnectedAttachment;
import zedi.pacbridge.app.net.UnexpectedlyClosedAttachement;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

@Singleton
@Startup
public class LostConnectionMonitor extends SynchronizedTracker implements StatisticTracker, Notifiable {

    /**
     * These two properties define the number of lost connection a site can have within a period of time
     * (measured in munites) before an alarm will be raised.  If more than 
     */
    public static final String LOST_CONNECTION_THRESHOLD_COUNT_PROPERTY_NAME = "bridgeMonitor.lostConnectionThresholdCount";
    public static final String LOST_CONNECTION_THRESHOLD_MINUTES_PROPERTY_NAME = "bridgeMonitor.lostConnectionThresholdMinutes";

    public static final Integer DEFAULT_LOST_CONNECTION_THRESHOLD_COUNT = 5;
    public static final Integer MIN_LOST_CONNECTION_THRESHOLD_COUNT = 1;
    
    public static final Integer DEFAULT_LOST_CONNECTION_THRESHOLD_MINUTES = 15;
    public static final Integer MIN_LOST_CONNECTION_THRESHOLD_MINUTES = 1;
    
    public static IntegerSystemProperty lostConnectionThesholdCountProperty = new IntegerSystemProperty(LOST_CONNECTION_THRESHOLD_COUNT_PROPERTY_NAME, 
                                                                                                        DEFAULT_LOST_CONNECTION_THRESHOLD_COUNT,
                                                                                                        MIN_LOST_CONNECTION_THRESHOLD_COUNT);
    public static IntegerSystemProperty lostConnectionThesholdMinutesProperty = new IntegerSystemProperty(LOST_CONNECTION_THRESHOLD_MINUTES_PROPERTY_NAME, 
                                                                                                          DEFAULT_LOST_CONNECTION_THRESHOLD_MINUTES,
                                                                                                          MIN_LOST_CONNECTION_THRESHOLD_MINUTES);
    
	private Cache<String, LostConnectionTracker> theCache;
    private Integer lostConnectionThesholdCount;
    private Integer lostConnectionThesholdMinutes;
    private SystemTime systemTime;
    
    public LostConnectionMonitor() {
    }

    @Inject
    public LostConnectionMonitor(NotificationCenter notificationCenter, Cache<String, LostConnectionTracker> theCache) {
        this(notificationCenter, theCache, SystemTime.SHARED_INSTANCE, lostConnectionThesholdCountProperty.currentValue(), lostConnectionThesholdMinutesProperty.currentValue());
    }
    
    public LostConnectionMonitor(NotificationCenter notificationCenter,
                                 Cache<String, LostConnectionTracker> theCache,
                                 SystemTime systemTime, 
                                 Integer lostConnectionThesholdCount, 
                                 Integer lostConnectionThesholdMinutes) {
        this.theCache = theCache;
        this.systemTime = systemTime;
        this.lostConnectionThesholdCount = lostConnectionThesholdCount;
        this.lostConnectionThesholdMinutes = lostConnectionThesholdMinutes;
        
        notificationCenter.addObserver(this, Connection.CONNECTION_LOST_NOTIFICATION);
        notificationCenter.addObserver(this, Connection.CONNECTION_CONNECTED_NOTIFICATION);
    }

    @Override
    public void handleNotification(Notification notification) {
        if (Connection.CONNECTION_CONNECTED_NOTIFICATION.equals(notification.getName()))
            removeTrackerForSiteAddress(notification.<SiteConnectedAttachment>getAttachment());
        else if (Connection.CONNECTION_LOST_NOTIFICATION.equals(notification.getName())) {
            UnexpectedlyClosedAttachement attachment = notification.getAttachment();
            recordLostConnection(attachment.getSiteAddress(), attachment.getException());
        }
    }
    
    public void recordLostConnection(SiteAddress siteAddress, Exception exception) {
        LostConnectionTracker tracker = lostConnectionTrackerForSite(siteAddress);
        tracker.recordLostConnection(exception, systemTime.getCurrentTime());
        theCache.replace(siteAddress.getAddress(), tracker);
    }
    
    public void removeTrackerForSiteAddress(SiteConnectedAttachment attachement) {
        theCache.remove(attachement.getSiteAddress().getAddress());
    }
    
    @Override
    public void reset() {
        theCache.clear();
    }
    
    
    private LostConnectionTracker lostConnectionTrackerForSite(SiteAddress siteAddress) {
        LostConnectionTracker tracker = theCache.get(siteAddress.getAddress());
        if (tracker == null) {
            tracker = new LostConnectionTracker(siteAddress, lostConnectionThesholdCount, lostConnectionThesholdMinutes);
            theCache.put(siteAddress.getAddress(), tracker);
        }
        return tracker;
    }

}
