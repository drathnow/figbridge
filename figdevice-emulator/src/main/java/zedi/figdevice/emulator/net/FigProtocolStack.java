package zedi.figdevice.emulator.net;

import java.io.IOException;

import zedi.pacbridge.net.CompressionLayer;
import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.PacketLayer;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.SecurityLayer;
import zedi.pacbridge.net.TcpNetworkAdapter;
import zedi.pacbridge.net.TransportLayer;

public class FigProtocolStack extends ProtocolStack {
    private FigSessionManager sessionManager;
    private TcpNetworkAdapter networkAdapter;
    
    public FigProtocolStack(FigSessionManager sessionLayer, 
                            PacketLayer packetLayer, 
                            CompressionLayer compressionLayer, 
                            SecurityLayer securityLayer, 
                            TransportLayer transportLayer,
                            FramingLayer framingLayer, 
                            TcpNetworkAdapter networkAdapter) {
        super(sessionLayer, packetLayer, compressionLayer, securityLayer, transportLayer, framingLayer, networkAdapter);
        this.sessionManager = sessionLayer;
        this.networkAdapter = networkAdapter;
    }
   
    public void sendUnsolicited(Message message) throws IOException {
        sessionManager.sendUnsolicited(message);
    }
    
    public boolean isActive() {
        return sessionManager.isActive();
    }

    
    public void connect() throws IOException {
        networkAdapter.start();
    }
    
    public void disconnect() {
        networkAdapter.close();
        sessionManager.reset();
    }
    
}
