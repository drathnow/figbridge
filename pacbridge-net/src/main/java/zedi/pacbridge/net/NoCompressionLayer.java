package zedi.pacbridge.net;

import java.io.IOException;


public class NoCompressionLayer implements CompressionLayer {
    private PacketLayer packetLayer;
    private SecurityLayer securityLayer;
    
    public NoCompressionLayer() {
    }

    @Override
    public void receive(ReceiveProtocolPacket packet) throws ProtocolException {
        packetLayer.receive(packet);
    }

    @Override
    public void setPacketLayer(PacketLayer packetLayer) {
        this.packetLayer = packetLayer;
    }

    @Override
    public void setSecurityLayer(SecurityLayer securityLayer) {
        this.securityLayer = securityLayer;
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        securityLayer.transmit(protocolPacket);
    }

    @Override
    public void start() throws IOException {
        securityLayer.start();
    }

    @Override
    public void close() {
        securityLayer.close();
    }

    @Override
    public void reset() {
        securityLayer.reset();
    }

}
