package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnSerializable;
import zedi.pacbridge.net.PacketHeader;
import zedi.pacbridge.utl.io.Unsigned;


public abstract class SwtHeader implements PacketHeader, GdnSerializable, Serializable {
    private static final long serialVersionUID = 1001L;
    
    private SwtHeaderType headerType;
    protected GdnMessageType messageType;

    protected SwtHeader(SwtHeaderType headerType) {
        this.headerType = headerType;
    }
    
    protected SwtHeader(SwtHeaderType headerType, GdnMessageType messageType) {
        this(headerType);
        this.messageType = messageType;
    }

    public GdnMessageType messageType() {
        return messageType;
    }
    
    public boolean supportsSession() {
        return headerType.supportsSession();
    }
    
    public SwtHeaderType headerType() {
        return headerType;
    }

    @Override
    public Integer getSessionId() {
        return 0;
    }
    
    public static final SwtHeader headerFromByteBuffer(ByteBuffer byteBuffer) {
        SwtHeaderType headerType = SwtHeaderType.headerTypeForVersionNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        SwtHeader header = headerForHeaderType(headerType);
        header.deserialize(byteBuffer);
        return header;
    }
    
    public static final SwtHeader headerForHeaderType(SwtHeaderType headerType) throws IllegalArgumentException {
        switch (headerType.getTypeNumber()) {
            case SwtHeaderType.HEADER_VERSION10:
                return new SwtHeader10();
            case SwtHeaderType.HEADER_VERSION12:
                return new SwtHeader12();
            default :
                throw new IllegalArgumentException("Invalid header version specified: " + headerType.toString());
        }
    }
}