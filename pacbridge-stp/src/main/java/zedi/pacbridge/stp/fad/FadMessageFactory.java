package zedi.pacbridge.stp.fad;

import java.nio.ByteBuffer;

public class FadMessageFactory {
    
    private int messageIndex = 1;
    
    public InTransitMessage newInTransitMessage(ByteBuffer byteBuffer, int maxPacketSize, int crc) {
        return new InTransitMessage(byteBuffer, maxPacketSize, crc, messageIndex++);
    }
    
    public PendingMessage newPendingMessageWithMessageId(int messageId) {
        return new PendingMessage(messageId);
    }
    
    public AckMessage newAckMessage(int messageId, int segmentId) {
        return new AckMessage(messageId, segmentId);        
    }

    public ResendMessageRequest newResendMessageRequest(int messageId, int segmentId) {
        return new ResendMessageRequest(messageId);
    }
}
