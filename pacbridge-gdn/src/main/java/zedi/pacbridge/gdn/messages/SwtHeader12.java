package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class SwtHeader12 extends SwtHeader implements SessionHeader, Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final int MAX_SESSION_ID = 65535;
    public static final int SIZE = 4;

    private int sessionId;

    protected SwtHeader12() {
        super(SwtHeaderType.Header12);
    }
    
    protected SwtHeader12(int sessionId, GdnMessageType messageType) {
        super(SwtHeaderType.Header12, messageType);
        if (sessionId > MAX_SESSION_ID)
            throw new IllegalArgumentException("Invalid session id.  Must be less than " + MAX_SESSION_ID);
        this.sessionId = sessionId;
    }

    @Override
    public boolean containsUnsolicitedMessage() {
        return false;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(headerType().getTypeNumber().byteValue());
        byteBuffer.put((byte)messageType().getNumber().intValue());
        byteBuffer.putShort((short)sessionId);
    }
    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        int messageNumber = Unsigned.getUnsignedByte(byteBuffer);
        this.messageType = GdnMessageType.messageTypeForMessageNumber(messageNumber);
        this.sessionId = Unsigned.getUnsignedShort(byteBuffer);
    }

}
