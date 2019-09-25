package zedi.pacbridge.net;

import java.io.IOException;

import zedi.pacbridge.net.auth.AuthenticationListener;

public class NoSecurityLayer implements SecurityLayer {
    private CompressionLayer compressionLayer;
    private FramingLayer framingLayer;
    
    @Override
    public void start() {
    }

    @Override
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException {
        compressionLayer.receive(protocolPacket);
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        framingLayer.transmit(protocolPacket);
    }

    @Override
    public void setCompressionLayer(CompressionLayer compressionLayer) {
        this.compressionLayer = compressionLayer;
    }

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }

    @Override
    public void close() {
        framingLayer.close();
    }

    @Override
    public void reset() {
        framingLayer.reset();
    }

    @Override
    public void setAuthenticationListener(AuthenticationListener listener) {
    }

}
