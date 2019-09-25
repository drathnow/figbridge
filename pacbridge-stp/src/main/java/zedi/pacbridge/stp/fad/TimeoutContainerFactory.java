package zedi.pacbridge.stp.fad;


class TimeoutContainerFactory {
    public PendingTimeoutContainer newPendingTimeoutContainer(PendingMessageTracker messageTracker, int messageId) {
        return new PendingTimeoutContainer(messageTracker, messageId);
    }
    
    public RetransmitRunner retransmitContainerForMessage(RetransmitEventHandler eventHandler, int messageId) {
        return new RetransmitRunner(eventHandler, messageId);
    }
}
