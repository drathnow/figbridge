package zedi.pacbridge.zap.messages;

import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.net.controls.ControlStatus;

public abstract class BaseControlResponseStrategy {
    private MessageType expectedMessageType;
    private Integer expectedSequenceNumber;
    private ControlStatus finalStatus;
    private String finalStatusMessage;

    public abstract boolean expectsAsyncReport();

    protected BaseControlResponseStrategy(MessageType expectedMessageType, Integer expectedSequenceNumber) {
        this.expectedMessageType = expectedMessageType;
        this.expectedSequenceNumber = expectedSequenceNumber;
        this.finalStatus = null;
        this.finalStatusMessage = null;
    }

    public ControlStatus finalStatus() {
        return finalStatus;
    }

    public String finalStatusMessage() {
        return finalStatusMessage;
    }
    
    public boolean isFinished() {
        return finalStatus != null;
    }
    
    protected void indicateCompletion(ControlStatus finalStatus, String finalStatusMessage) {
        this.finalStatus = finalStatus;
        this.finalStatusMessage = finalStatusMessage;
    }
    
    protected boolean isNotProtocolError(AckMessage message) {
        if (message.isProtocolError()) {
            indicateCompletion(ControlStatus.FAILURE, message.additionalDetails().toString());
            return false;
        }
        return true;
    }
    
    protected boolean isExpectedMessage(AckMessage ackMessage) {
        return ackMessage.getAckedMessageType().equals(expectedMessageType) && ackMessage.sequenceNumber() == expectedSequenceNumber;
    }
    
}
