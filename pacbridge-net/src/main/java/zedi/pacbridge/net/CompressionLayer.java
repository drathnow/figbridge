package zedi.pacbridge.net;

import java.io.IOException;

public interface CompressionLayer {
    void receive(ReceiveProtocolPacket packet) throws ProtocolException;
    void setPacketLayer(PacketLayer packetLayer);
    void setSecurityLayer(SecurityLayer securityLayer);
    void transmit(TransmitProtocolPacket protocolPacket) throws IOException;
    void start() throws IOException;
    void close();
    void reset();
}
