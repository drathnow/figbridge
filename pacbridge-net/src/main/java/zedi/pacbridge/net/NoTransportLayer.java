package zedi.pacbridge.net;

import java.io.IOException;


public class NoTransportLayer implements TransportLayer {
    private FramingLayer framingLayer;
    private SecurityLayer securityLayer;
    
    @Override
    public void setSecurityLayer(SecurityLayer securityLayer) {
        this.securityLayer = securityLayer;
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        framingLayer.transmit(protocolPacket);
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

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }

    @Override
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
        securityLayer.receive(protocolPacket);
    }

    @Override
    public boolean isActive() {
        return false;
    }

}
