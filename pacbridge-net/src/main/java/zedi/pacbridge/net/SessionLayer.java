package zedi.pacbridge.net;

import java.io.IOException;

import zedi.pacbridge.utl.SiteAddress;

public interface SessionLayer {
    Session newSession();
    void setPacketLayer(PacketLayer packetLayer);
    void receive(ReceiveProtocolPacket protocolPacket, Integer messageType, Integer sequenceNumber, Integer sessionId);
    void start() throws IOException;
    void close();
    void reset();
    void setSiteAddress(SiteAddress siteAddress);
}
