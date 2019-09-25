package zedi.pacbridge.gdn;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.gdn.messages.GdnMessageFactory;
import zedi.pacbridge.gdn.messages.GdnPacket;
import zedi.pacbridge.gdn.messages.SwtHeader;
import zedi.pacbridge.gdn.messages.SwtHeaderFactory;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.PacketDecoder;
import zedi.pacbridge.net.annotations.PacketLayerDecoder;

@PacketLayerDecoder(forNetworkType="GDN")
public class GdnPacketDecoder implements PacketDecoder {
    private SwtHeaderFactory headerFactory;
    private GdnMessageFactory messageFactory;

    GdnPacketDecoder(SwtHeaderFactory headerFactory, GdnMessageFactory messageFactory) {
        this.headerFactory = headerFactory;
        this.messageFactory = messageFactory;
    }    
    
    public GdnPacketDecoder() {
        this(new SwtHeaderFactory(), new GdnMessageFactory());
    }
    
    
    @Override
    public Packet packetForByteBuffer(ByteBuffer byteBuffer) {
        SwtHeader header = headerFactory.headerFromByteBuffer(byteBuffer);
        GdnMessage message = messageFactory.messageFromByteBuffer(header.messageType().getNumber(), byteBuffer);
        return new GdnPacket(header, message);
    }
}
