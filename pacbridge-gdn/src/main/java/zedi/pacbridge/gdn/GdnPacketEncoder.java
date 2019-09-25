package zedi.pacbridge.gdn;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.gdn.messages.GdnMessageType;
import zedi.pacbridge.gdn.messages.GdnPacket;
import zedi.pacbridge.gdn.messages.SwtHeader;
import zedi.pacbridge.gdn.messages.SwtHeaderFactory;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.PacketEncoder;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.net.annotations.PacketLayerEncoder;

@PacketLayerEncoder(forNetworkType="GDN")
public class GdnPacketEncoder implements PacketEncoder {

    private SwtHeaderFactory headerFactory;
    
    GdnPacketEncoder(SwtHeaderFactory headerFactory) {
        this.headerFactory = headerFactory;
    }
    
    public GdnPacketEncoder() {
        this(new SwtHeaderFactory());
    }

    @Override
    public Packet packetForMessageAndSession(Message message, Session session) {
        SwtHeader header = headerFactory.newSessionHeaderWithSessionIdAndMessageType(session.getSessionId(), (GdnMessageType)message.messageType());
        return new GdnPacket(header, (GdnMessage)message);
    }

    @Override
    public Packet packetForMessage(Message message) {
        SwtHeader header = headerFactory.newSessionlessHeaderWithMessageType((GdnMessageType)message.messageType());
        return new GdnPacket(header, (GdnMessage)message);
    }
}
