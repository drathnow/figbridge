package zedi.pacbridge.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import zedi.pacbridge.utl.SiteAddress;

public interface NetworkAdapter {
    void setNetworkAdapterListener(NetworkAdapterListener listener);
    void setFramingLayer(FramingLayer framingLayer);
    void transmit(TransmitProtocolPacket protocolPacket) throws IOException;
    void start() throws IOException;
    void close();
    void reset();
    long getLastActivityTime();
    int getBytesReceived();
    InetSocketAddress getRemoteAddress();
    int getBytesTransmitted();
    void setSiteAddress(SiteAddress siteAddress);
}