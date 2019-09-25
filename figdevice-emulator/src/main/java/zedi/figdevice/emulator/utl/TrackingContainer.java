package zedi.figdevice.emulator.utl;

import java.util.concurrent.ScheduledFuture;

public class TrackingContainer implements Comparable<TrackingContainer> {
    private Integer sequenceNumber;
    private Long dueTime;
    private ScheduledFuture<?> future;
    
    public TrackingContainer(Integer sequenceNumber, Integer timeoutSeconds, ScheduledFuture<?> future) {
        this.sequenceNumber = sequenceNumber;
        this.dueTime = System.currentTimeMillis() + (timeoutSeconds*1000);
        this.future = future;
    }
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
    
    public void cancel() {
        future.cancel(false);
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() >= dueTime;
    }

    @Override
    public int compareTo(TrackingContainer otherContainer) {
        return dueTime.compareTo(otherContainer.dueTime);
    }
}
