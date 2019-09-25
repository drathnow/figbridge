package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface FramingLayer {
    public void setTransportLayer(TransportLayer transportLayer);
    public void setNetworkAdapter(NetworkAdapter adapter);
    public void receive(ByteBuffer byteBuffer) throws ProtocolException;
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException;
    public void start() throws IOException;
    public void close();
    public void reset();
    public boolean isActive();
}