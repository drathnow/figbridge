package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class SwtHeader10 extends SwtHeader {

    public static final int SIZE = 2;

    protected SwtHeader10() {
        super(SwtHeaderType.Header10);
    }
    
    protected SwtHeader10(GdnMessageType messageType) {
        super(SwtHeaderType.Header10, messageType);
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(headerType().getTypeNumber().byteValue());
        byteBuffer.put((byte)messageType().getNumber().intValue());
    }
    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        int messageNumber = Unsigned.getUnsignedByte(byteBuffer);
        messageType = GdnMessageType.messageTypeForMessageNumber(messageNumber);
    }

    @Override
    public boolean containsUnsolicitedMessage() {
        return true;
    }
}
