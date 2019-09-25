package zedi.pacbridge.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import zedi.pacbridge.net.auth.AuthenticationListener;
import zedi.pacbridge.utl.SiteAddress;

public class ProtocolStack {
    private SessionLayer sessionLayer;
    private PacketLayer packetLayer;
    private CompressionLayer compressionLayer;
    private SecurityLayer securityLayer;
    private TransportLayer transportLayer;
    private FramingLayer framingLayer;
    private NetworkAdapter networkAdapter;
    
    public ProtocolStack(SessionLayer sessionLayer, 
                         PacketLayer packetLayer, 
                         CompressionLayer compressionLayer, 
                         SecurityLayer securityLayer, 
                         TransportLayer transportLayer,
                         FramingLayer framingLayer, 
                         NetworkAdapter networkAdapter) {
        this.sessionLayer = sessionLayer;
        this.packetLayer = packetLayer;
        this.compressionLayer = compressionLayer;
        this.securityLayer = securityLayer;
        this.transportLayer = transportLayer;
        this.framingLayer = framingLayer;
        this.networkAdapter = networkAdapter;
        
        this.sessionLayer.setPacketLayer(packetLayer);
        this.packetLayer.setSessionManager(sessionLayer);
        this.packetLayer.setCompressionLayer(compressionLayer);
        this.compressionLayer.setPacketLayer(packetLayer);
        this.compressionLayer.setSecurityLayer(securityLayer);
        this.securityLayer.setCompressionLayer(compressionLayer);
        this.securityLayer.setFramingLayer(framingLayer);
        this.transportLayer.setSecurityLayer(securityLayer);
        this.transportLayer.setFramingLayer(framingLayer);
        this.framingLayer.setTransportLayer(transportLayer);
        this.framingLayer.setNetworkAdapter(networkAdapter);
        this.networkAdapter.setFramingLayer(framingLayer);
    }
    
    public int getBytesReceived() {
        return this.networkAdapter.getBytesReceived();
    }
    
    public int getBytesTransmitted() {
        return this.networkAdapter.getBytesTransmitted();
    }
    
    public InetSocketAddress getRemoteAddress() {
        return this.networkAdapter.getRemoteAddress();
    }
    
    public Session newSession() {
        return sessionLayer.newSession();
    }
    
    public void setNetworkAdapterListener(NetworkAdapterListener listener) {
        this.networkAdapter.setNetworkAdapterListener(listener);
    }
    
    public void setAuthenticationListener(AuthenticationListener listener) {
        this.securityLayer.setAuthenticationListener(listener);
    }
    
    public void start() throws IOException {
        sessionLayer.start();
    }
    
    public void close() {
        sessionLayer.close();
    }

    public void setSiteAddress(SiteAddress siteAddress) {
        sessionLayer.setSiteAddress(siteAddress);
        networkAdapter.setSiteAddress(siteAddress);
    }

    public Long getLastActivityTime() {
        return networkAdapter.getLastActivityTime();
    }
    
}
