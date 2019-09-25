package zedi.pacbridge.net;

import java.io.IOException;

import zedi.pacbridge.net.auth.AuthenticationListener;

public interface SecurityLayer {
    void setAuthenticationListener(AuthenticationListener listener);
    void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException;
    void transmit(TransmitProtocolPacket protocolPacket) throws IOException;
    void setCompressionLayer(CompressionLayer compressionLayer);
    void setFramingLayer(FramingLayer framingLayer);
    void start() throws IOException;
    void close();
    void reset();
}