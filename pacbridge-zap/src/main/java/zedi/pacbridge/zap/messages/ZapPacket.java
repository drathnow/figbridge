package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.PacketHeader;
import zedi.pacbridge.utl.DependencyResolver;

public class ZapPacket implements Packet {
    private ZapPacketHeader packetHeader;
    private Message message;
    
    public ZapPacket(ZapPacketHeader packetHeader, Message message) {
        this.packetHeader = packetHeader;
        this.message = message;
    }

    public void serialize(ByteBuffer byteBuffer) {
        serialize(packetHeader, message, byteBuffer);
    }
    
    public static void serialize(ZapPacketHeader packetHeader, Message message, ByteBuffer byteBuffer) {
        packetHeader.serialize(byteBuffer);
        message.serialize(byteBuffer);
    }

    public static ZapPacket packetFromByteBuffer(ByteBuffer byteBuffer) {
        FieldTypeLibrary fieldTypeLibray = null;
        try {
            fieldTypeLibray = DependencyResolver.Implementation.sharedInstance().getImplementationOf(FieldTypeLibrary.class);
        } catch (Exception e) {
        }
        ZapMessageFactory messageFactory = new ZapMessageFactory(fieldTypeLibray);
        ZapPacketHeader header = ZapPacketHeader.packetHeaderFromByteBuffer(byteBuffer);
        Message message = messageFactory.messageFromByteBuffer(header.messageType().getNumber(), byteBuffer);
        if (header.headerType() == ZapHeaderType.SESSION_HEADER)
            message.setSequenceNumber(((SessionHeader)header).getSequenceNumber());
        return new ZapPacket(header, message);
    }

    public static ZapPacket packetFromByteBuffer(ByteBuffer byteBuffer, FieldTypeLibrary fieldTypeLibrary) {
        ZapMessageFactory messageFactory = new ZapMessageFactory(fieldTypeLibrary);
        ZapPacketHeader header = ZapPacketHeader.packetHeaderFromByteBuffer(byteBuffer);
        Message message = messageFactory.messageFromByteBuffer(header.messageType().getNumber(), byteBuffer);
        if (header.headerType() == ZapHeaderType.SESSION_HEADER)
            message.setSequenceNumber(((SessionHeader)header).getSequenceNumber());
        return new ZapPacket(header, message);
    }

    @Override
    public boolean containsUnsolicitedMessage() {
        return packetHeader.getSessionId() == 0;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public PacketHeader getHeader() {
        return packetHeader;
    }
}
