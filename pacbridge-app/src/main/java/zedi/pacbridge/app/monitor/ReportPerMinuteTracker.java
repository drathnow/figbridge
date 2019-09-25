package zedi.pacbridge.app.monitor;

import zedi.pacbridge.zap.reporting.ZapReport;

public class ReportPerMinuteTracker extends SynchronizedTracker implements StatisticTracker {
    private int[] reportsTrackingArray = new int[MINUTES_IN_A_DAY];
    private int[] readingsTrackingArray = new int[MINUTES_IN_A_DAY];

    public ReportPerMinuteTracker() {
        reset();
    }
    
    public void recordReport(ZapReport report) {
        lock();
        reportsTrackingArray[Stats.currentMinuteOfDay()]++;
        readingsTrackingArray[Stats.currentMinuteOfDay()] += report.numberOfReadings();
        unlock();
    }
    
    public int[] getReportsTrackingArray() {
        int[] copy = new int[MINUTES_IN_A_DAY];
        lock();
        System.arraycopy(reportsTrackingArray, 0, copy, 0, MINUTES_IN_A_DAY);
        unlock();
        return copy;
    }

    public int[] getReadingsTrackingArray() {
        int[] copy = new int[MINUTES_IN_A_DAY];
        lock();
        System.arraycopy(readingsTrackingArray, 0, copy, 0, MINUTES_IN_A_DAY);
        unlock();
        return copy;
    }
    
    @Override
    public void reset() {
        lock();
        for (int i = 0; i < reportsTrackingArray.length; i++) {
            reportsTrackingArray[i] = -1;
            readingsTrackingArray[i] = -1;
        }
        unlock();
    }

}
