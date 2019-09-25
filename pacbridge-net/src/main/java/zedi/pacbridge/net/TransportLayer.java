package zedi.pacbridge.net;

import java.io.IOException;

public interface TransportLayer {
    public void setSecurityLayer(SecurityLayer securityLayer);
    public void setFramingLayer(FramingLayer framingLayer);
    public void receive(ReceiveProtocolPacket protocolPacket) throws ProtocolException;
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException;
    public void start() throws IOException;
    public void close();
    public void reset();
    public boolean isActive();
}
