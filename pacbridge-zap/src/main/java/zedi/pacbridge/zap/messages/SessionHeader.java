package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class SessionHeader extends ZapPacketHeader {
    private Integer sessionId = 0;
    private Integer sequenceNumber = 0;
    
    public SessionHeader(MessageType messageType) {
        super(ZapHeaderType.SESSION_HEADER, messageType);
    }

    
    public SessionHeader(ZapMessageType messageType, Integer sessionId, Integer sequenceNumber) {
        super(ZapHeaderType.SESSION_HEADER, messageType);
        this.sessionId = sessionId;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public Integer getSessionId() {
        return sessionId;
    }
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(headerType().getTypeNumber().byteValue());
        byteBuffer.putShort(messageType().getNumber().shortValue());
        byteBuffer.putShort(sessionId.shortValue());
        byteBuffer.putShort(sequenceNumber.shortValue());
    }
    
    public static SessionHeader sessionHeaderFromByteBuffer(ByteBuffer byteBuffer) {
        ZapMessageType type = ZapMessageType.messageTypeForNumber(Unsigned.getUnsignedShort(byteBuffer));
        Integer sessionId = Unsigned.getUnsignedShort(byteBuffer);
        Integer sequenceNumber = Unsigned.getUnsignedShort(byteBuffer);
        return new SessionHeader(type, sessionId, sequenceNumber);
    }


    @Override
    public boolean containsUnsolicitedMessage() {
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Version     : ").append(headerType()).append('\n')
                      .append("Message Type: ").append(messageType()).append('\n')
                      .append("Session Id  : ").append(sessionId).append('\n')
                      .append("Sequence No.: ").append(sequenceNumber);
        return stringBuilder.toString();
    }
}
