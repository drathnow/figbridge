package zedi.figdevice.emulator.utl;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;

public class MessageTracker {
    public static final String TIMEDOUT_MESSAGE_NOTIFICATION = "MessageTimedOut";
    private static final Logger logger = LoggerFactory.getLogger(MessageTracker.class.getName());
    
    private final Lock lock;
    private Map<Integer, TrackingContainer> containerMap;
    private Integer timeoutSeconds;
    private PriorityQueue<TrackingContainer> queue;
    private GlobalScheduledExecutor scheduledExecutor;
    private NotificationCenter notificationCenter;
    private ExpiredMessageRunner expiredMessageRunner;
    
    public MessageTracker(Integer timeoutSeconds, GlobalScheduledExecutor scheduledExecutor, NotificationCenter notificationCenter) {
        this.lock = new ReentrantLock();
        this.timeoutSeconds = timeoutSeconds;
        this.containerMap = new TreeMap<Integer, TrackingContainer>();
        this.queue = new PriorityQueue<>();
        this.scheduledExecutor = scheduledExecutor;
        this.notificationCenter = notificationCenter;
        this.expiredMessageRunner = new ExpiredMessageRunner();
    }

    public void trackMessage(Integer sequenceNumber) {
        lock.lock();
        try {
            ScheduledFuture<?> future = scheduledExecutor.schedule(expiredMessageRunner, timeoutSeconds);
            TrackingContainer trackingContainer = new TrackingContainer(sequenceNumber, timeoutSeconds, future);
            containerMap.put(sequenceNumber, trackingContainer);
            queue.add(trackingContainer);
            future.cancel(false);
        } finally {
            lock.unlock();
        }
    }
    
    public void stopTrackingContainerWithSequenceNumber(Integer sequenceNumber) {
        lock.lock();
        try {
            TrackingContainer removed = containerMap.remove(sequenceNumber);
            if (removed != null) {
                queue.remove(removed);
                removed.cancel();
            } else
                logger.warn("Unable to stop tracking message. Unknown sequence number: " + sequenceNumber);
        } finally {
            lock.unlock();
        }
    }
    
    class ExpiredMessageRunner implements Runnable {

        @Override
        public void run() {
            lock.lock();
            try {
                while (queue.peek().hasExpired()) {
                    TrackingContainer container = queue.poll();
                    containerMap.remove(container);
                    container.cancel();
                    logger.error("Message was not acknowledge within timeout. Sequence Number = " + container.getSequenceNumber());
                    notificationCenter.postNotification(TIMEDOUT_MESSAGE_NOTIFICATION, container.getSequenceNumber());
                }
            } finally {
                lock.unlock();
            }
        }
        
    }

    public boolean hasOutstandingAcks() {
        return containerMap.size() > 0;
    }
}
