package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.HeaderType;
import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.net.PacketHeader;
import zedi.pacbridge.utl.io.Unsigned;

public abstract class ZapPacketHeader implements PacketHeader {
    private HeaderType headerType;
    private MessageType messageType;
    
    public ZapPacketHeader(HeaderType headerType, MessageType messageType) {
        this.headerType = headerType;
        this.messageType = messageType;
    }

    public abstract void serialize(ByteBuffer byteBuffer);

    public MessageType messageType() { 
        return messageType;
    }
    
    public HeaderType headerType() {
        return headerType;
    }
    
    public static ZapPacketHeader packetHeaderFromByteBuffer(ByteBuffer byteBuffer) {
        Integer type = (int)Unsigned.getUnsignedByte(byteBuffer);
        switch (type) {
            case ZapHeaderType.SESSION_HEADER_NUMBER : return SessionHeader.sessionHeaderFromByteBuffer(byteBuffer);
        }
        throw new IllegalArgumentException("Unknow header type '" + type + "'");
    }
}
