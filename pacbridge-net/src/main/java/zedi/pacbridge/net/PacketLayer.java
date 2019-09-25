package zedi.pacbridge.net;

import java.io.IOException;

public interface PacketLayer {
    void transmit(TransmitProtocolPacket protocolPacket, Integer messageType, Integer sequenceNumber, Integer sessionId) throws IOException;
    void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException;
    void setSessionManager(SessionLayer sessionManager);
    void setCompressionLayer(CompressionLayer compressionLayer);
    void start() throws IOException;
    void close();
    void reset();
}
