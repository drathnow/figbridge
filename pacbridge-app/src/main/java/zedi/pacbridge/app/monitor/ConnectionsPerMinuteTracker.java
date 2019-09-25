package zedi.pacbridge.app.monitor;

import java.io.Serializable;



public class ConnectionsPerMinuteTracker extends SynchronizedTracker implements StatisticTracker, Serializable {
    private int[] trackingArray = new int[MINUTES_IN_A_DAY];
    
    public ConnectionsPerMinuteTracker() {
        reset();
    }
    
    public synchronized void incrementConnectCount() {
        lock();
        trackingArray[Stats.currentMinuteOfDay()]++;
        unlock();
    }
    
    public int[] getTrackingArray() {
        lock();
        int[] copy = new int[MINUTES_IN_A_DAY];
        System.arraycopy(trackingArray, 0, copy, 0, MINUTES_IN_A_DAY);
        unlock();
        return copy;
    }

    @Override
    public void reset() {
        lock();
        for (int i = 0; i < trackingArray.length; i++)
            trackingArray[i] = -1;
        unlock();
    }
}
