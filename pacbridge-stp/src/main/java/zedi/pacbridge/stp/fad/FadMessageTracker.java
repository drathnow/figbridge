package zedi.pacbridge.stp.fad;

import java.util.concurrent.TimeoutException;

import zedi.pacbridge.net.MessageTracker;

public class FadMessageTracker implements MessageTracker {
    private InTransitMessage inTransitMessage;
    
    public FadMessageTracker(InTransitMessage inTransitMessage) {
        this.inTransitMessage = inTransitMessage;
    }

    @Override
    public boolean hasBeenAcknowledged() {
        return inTransitMessage.hasBeenAcknowledged();
    }
    
    @Override
    public boolean hasFailed() {
        return inTransitMessage.hasFailed();
    }
    
    @Override
    public Integer messageId() {
        return inTransitMessage.getMessageId();
    }

    @Override
    public boolean hasFinished() {
        return hasFailed() || hasBeenAcknowledged();
    }
    
    @Override
    public void waitForSuccessOrFailure(long timeoutMilliseconds) throws TimeoutException {
        long expiredTime = System.currentTimeMillis() + timeoutMilliseconds;
        try {
            while (notTimeToReturn(expiredTime))
                Thread.sleep(100L);
        } catch (InterruptedException e) {
        }
        if (hasFinished() == false)
            throw new TimeoutException();
    }
    
    private boolean notTimeToReturn(long expiredTime) {
        return hasFinished() == false && System.currentTimeMillis() < expiredTime;
    }
}