package zedi.pacbridge.app.monitor;

import java.io.Serializable;

import zedi.pacbridge.utl.stats.MovingAverage;

public class PublishingTimeTracker extends SynchronizedTracker implements StatisticTracker, Serializable {
    private long[] trackingArray = new long[MINUTES_IN_A_DAY];
    private MovingAverage movingAverage = new MovingAverage(100);
    
    public PublishingTimeTracker() {
        reset();
    }
    
    public void addPublishingTime(long timeInMilliseconds) {
        synchronized (movingAverage) {
            movingAverage.addSample(timeInMilliseconds);
            trackingArray[Stats.currentMinuteOfDay()] = movingAverage.getAverage().longValue();
        }
    }
    
    @Override
    public void reset() {
        lock();
        for (int i = 0; i < trackingArray.length; i++)
            trackingArray[i] = -1;
        unlock();
    }
}
