package zedi.pacbridge.app.monitor;

import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.net.Report;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;

@Singleton
@Startup
public class BridgeMonitor {

    public static final String DUPLICATE_SITE_REPORT_ALARM_NOTIFICATION_NAME = "bridgeAlarm.duplicateReportsDetected";
    public static final String DISBALE_DUPLICATE_REPORT_DETECTION_PROPERTY_NAME = "bridgeMonitor.disableDuplicateReportDetection";

    private Map<SiteAddress, DuplicateReportTracker> trackerMap = new TreeMap<SiteAddress, DuplicateReportTracker>();

    private boolean disableCheck = Boolean.getBoolean(DISBALE_DUPLICATE_REPORT_DETECTION_PROPERTY_NAME);
    private int duplicateAlarmCountThreshold;
    private DuplicateReportMonitor duplicateReportMonitor;
    
    public BridgeMonitor() {
    }
    
    @Inject
    public BridgeMonitor(GlobalScheduledExecutor scheduledExecutor, NotificationCenter notificationCenter) {
        duplicateReportMonitor = new DuplicateReportMonitor(this, scheduledExecutor, notificationCenter);
        duplicateReportMonitor.scheduleNextRun();
    }

    public boolean isDuplicateReportForSite(Report reportMessage, SiteAddress siteAddress) {
        if (disableCheck == false) {
            DuplicateReportTracker tracker;
            synchronized (trackerMap) {
                tracker = trackerMap.get(siteAddress);
                if (tracker == null) {
                    tracker = new DuplicateReportTracker(siteAddress);
                    trackerMap.put(siteAddress, tracker);
                }
            }
            return tracker.isDuplicateReport(reportMessage);
        }
        return false;
    }

    Map<SiteAddress, Integer> sitesWithDuplicatePollsetReports() {
        Map<SiteAddress, Integer> resultMap = new TreeMap<SiteAddress, Integer>();
        synchronized (trackerMap) {
            for (DuplicateReportTracker tracker : trackerMap.values()) {
                int duplicates = tracker.getDuplicateReportCount();
                if (duplicates > 0)
                    resultMap.put(tracker.address, duplicates);
            }
        }
        return resultMap;
    }

    public void setDuplicateAlarmCountThreshold(int duplicateAlarmCountThreshold) {
        this.duplicateAlarmCountThreshold = duplicateAlarmCountThreshold;
    }

    public int getDuplicateAlarmCountThreshold() {
        return duplicateAlarmCountThreshold;
    }
}