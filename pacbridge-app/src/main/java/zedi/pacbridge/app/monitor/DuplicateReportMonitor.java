package zedi.pacbridge.app.monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;

class DuplicateReportMonitor implements Runnable {

    private static Logger logger = Logger.getLogger(DuplicateReportMonitor.class);
    
    public static final long ALARM_TIMEOUT_PERIOD = TimeUnit.MINUTES.toMillis(60);
    public static final long SCAN_INTERVAL_MINUTES = 1;
    
    Map<SiteAddress, Long> alarmMap = new TreeMap<SiteAddress, Long>();

    private BridgeMonitor bridgeMonitor;
    private GlobalScheduledExecutor scheduledExecutor;
    private NotificationCenter notificationCenter;
    
    public DuplicateReportMonitor(BridgeMonitor bridgeMonitor, GlobalScheduledExecutor scheduledExecutor, NotificationCenter notificationCenter) {
        this.bridgeMonitor = bridgeMonitor;
        this.notificationCenter = notificationCenter;
        this.scheduledExecutor = scheduledExecutor;
    }
    
    @Override
    public void run() {
        Map<SiteAddress, Integer> duplicates = bridgeMonitor.sitesWithDuplicatePollsetReports();
        if (duplicates != null) {
            removeDuplicateAlarms(duplicates);
            if (duplicates.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Duplicate reports detected for site(s): ");
                for (SiteAddress siteAddress : duplicates.keySet()) {
                    stringBuilder.append('(')
                        .append(siteAddress.toString())
                        .append('|')
                        .append(duplicates.get(siteAddress))
                        .append(')')
                        .append(',');
                }
                stringBuilder.setLength(stringBuilder.length()-1);
                logger.warn(stringBuilder.toString());
                notificationCenter.postNotification(BridgeMonitor.DUPLICATE_SITE_REPORT_ALARM_NOTIFICATION_NAME, duplicates);
            }
        }
        
        removeExpiredAlarms();
        scheduleNextRun();
    }

    void scheduleNextRun() {
        scheduledExecutor.schedule(this, SCAN_INTERVAL_MINUTES, TimeUnit.SECONDS);
    }
    
    private void removeDuplicateAlarms(Map<SiteAddress, Integer> duplicates) {
        for (Iterator<SiteAddress> iterator = duplicates.keySet().iterator(); iterator.hasNext(); ) {
            SiteAddress address = iterator.next();
            Long alarmTime = alarmMap.get(address);
            if (alarmTime == null) {
                alarmTime = System.currentTimeMillis();
                alarmMap.put(address, alarmTime);
            } else if (hasAlarmTimeExpired(alarmTime) == false)
                iterator.remove();
            else
                alarmMap.put(address, System.currentTimeMillis());
        }
    }

    private void removeExpiredAlarms() {
        for (Iterator<Long> iterator = alarmMap.values().iterator(); iterator.hasNext(); ) {
            if (hasAlarmTimeExpired(iterator.next()))
                iterator.remove();
        }
    }

    private boolean hasAlarmTimeExpired(Long alarmTime) {
        return System.currentTimeMillis() - alarmTime > ALARM_TIMEOUT_PERIOD;
    }
}
