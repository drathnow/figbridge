package zedi.pacbridge.stp.fad;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.CollectionFactory;
import zedi.pacbridge.utl.InactivityStrategy;
import zedi.pacbridge.utl.crc.CrcException;

public class PendingMessageTracker extends BaseTracker {
    private static Logger logger = LoggerFactory.getLogger(PendingMessageTracker.class);
    
    private Map<Integer, PendingMessage> pendingMessagesMap;
    private Map<Integer, ScheduledFuture<?>> pendingMessagesTimerMap;

    private TimeoutContainerFactory timeoutContainerFactory;
    private FadMessageFactory messageFactory;
    private InactivityStrategy inactivityStrategy;
    private final Lock lock;

    public PendingMessageTracker(InactivityStrategy inactivityStrategy) {
        this(inactivityStrategy, new CollectionFactory(), new ReentrantLock());
    }
    
    @SuppressWarnings("unchecked")
    public PendingMessageTracker(InactivityStrategy inactivityStrategy, CollectionFactory collectionFactory, Lock lock) {
        setPendingMessageInactivityStrategy(inactivityStrategy);
        this.pendingMessagesMap = collectionFactory.newTreeMap(new TreeMap<Integer, PendingMessage>());
        this.pendingMessagesTimerMap = collectionFactory.newTreeMap(new TreeMap<Integer, ScheduledFuture<?>>());
        this.timeoutContainerFactory = new TimeoutContainerFactory();
        this.messageFactory = new FadMessageFactory();
        this.lock = lock;
    }

    public void setTimeoutContainerFactory(TimeoutContainerFactory timeoutContainerFactory) {
        this.timeoutContainerFactory = timeoutContainerFactory;
    }
    
    public byte[] payloadForSegmentMessageIfComplete(Segment segment) throws CrcException {
        lock.lock();
        try {
            int messageId = segment.getMessageId();
            PendingMessage pendingMessage = pendingMessageFromTrackingMap(messageId);
            pendingMessage.addSegment(segment);
            return payloadIfMessageIsComplete(pendingMessage);
        } finally {
            lock.unlock();
        }
    }
    
    public void setPendingMessageInactivityStrategy(InactivityStrategy inactivityStrategy) {
        this.inactivityStrategy = inactivityStrategy;
    }

    public int getPendingMessagesCount() {
        return pendingMessagesMap.size();
    }
    
    public boolean isIdle() {
        return getPendingMessagesCount() == 0;
    }
    
    public void setMessageFactory(FadMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }
    
    public void handleTimeoutForPendingMessageWithMessageId(int messageId) {
        lock.lock();
        try {
            removeAndCancelPendingTimerWithMessageIdFromMap(messageId, pendingMessagesTimerMap);
            pendingMessagesMap.remove(messageId);
            logger.debug("Pending message timed out. Message ID: " + messageId);
        } finally {
            lock.unlock();
        }
    }    

    public void clear() {
        lock.lock();
        try {
            for (ScheduledFuture<?> future : pendingMessagesTimerMap.values())
                future.cancel(false);
            pendingMessagesTimerMap.clear();
            pendingMessagesMap.clear();
        } finally {
            lock.unlock();
        }
    }

    private byte[] payloadIfMessageIsComplete(PendingMessage pendingMessage) throws CrcException {
        byte[] messagePayload = null;
        if (pendingMessage.isComplete()) {
            try {
                messagePayload = pendingMessage.getMessage();
            } catch (CrcException e) {
                logger.error("Unable to decode message with Id " + pendingMessage.getMessageId() 
                        + ": " + e.toString());
                
            } finally {
                pendingMessagesMap.remove(pendingMessage.getMessageId());
                removeAndCancelPendingTimerWithMessageIdFromMap(pendingMessage.getMessageId(), pendingMessagesTimerMap);
            }
        }
        return messagePayload;
    }
    
    private PendingMessage pendingMessageFromTrackingMap(int messageId) {
        PendingMessage pendingMessage = pendingMessagesMap.get(messageId);
        if (pendingMessage == null) {
            pendingMessage = messageFactory.newPendingMessageWithMessageId(messageId);
            pendingMessagesMap.put(messageId, pendingMessage);
            trackPendingMessage(pendingMessage);
        }
        return pendingMessage;
    }

    private void trackPendingMessage(PendingMessage pendingMessage) {
        int messageId = pendingMessage.getMessageId();
        PendingTimeoutContainer container = timeoutContainerFactory.newPendingTimeoutContainer(this, messageId);
//        ScheduledFuture<?> future = inactivityStrategy.scheduleInactivityRunner(container);
//        pendingMessagesTimerMap.put(messageId, future);
    }    
}
