package zedi.pacbridge.stp.fad;

class PendingTimeoutContainer implements Runnable {
    private PendingMessageTracker messageTracker;
    private int messageId;
    
    public PendingTimeoutContainer(PendingMessageTracker messageTracker, int messageId) {
        super();
        this.messageTracker = messageTracker;
        this.messageId = messageId;
    }
    
    @Override
    public void run() {
        messageTracker.handleTimeoutForPendingMessageWithMessageId(messageId);
    }
    
}
