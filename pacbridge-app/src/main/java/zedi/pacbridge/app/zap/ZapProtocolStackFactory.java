package zedi.pacbridge.app.zap;

import java.net.InetSocketAddress;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.auth.DefaultSessionKeyGenerator;
import zedi.pacbridge.app.auth.zap.ZapAuthenticationStrategy;
import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.app.net.ProtocolStackFactory;
import zedi.pacbridge.app.util.LookupHelper;
import zedi.pacbridge.net.CompressionLayer;
import zedi.pacbridge.net.CountedByteFramingLayer;
import zedi.pacbridge.net.DecrementingSessionIdGenerator;
import zedi.pacbridge.net.DefaultSecurityLayer;
import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.NoCompressionLayer;
import zedi.pacbridge.net.NoTransportLayer;
import zedi.pacbridge.net.PacketLayer;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.SecurityLayer;
import zedi.pacbridge.net.SessionIdGenerator;
import zedi.pacbridge.net.SessionManager;
import zedi.pacbridge.net.SessionlessMessageHandler;
import zedi.pacbridge.net.TransmitProtocolPacket;
import zedi.pacbridge.net.TransportLayer;
import zedi.pacbridge.net.UnsolicitedMessageHandler;
import zedi.pacbridge.net.annotations.Protocol;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.io.Sha1Hasher;
import zedi.pacbridge.zap.ZapPacketLayer;
import zedi.pacbridge.zap.ZapUnsolicitedMessageHandler;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ZapMessageFactory;

@Protocol(name = "zap")
public class ZapProtocolStackFactory implements ProtocolStackFactory {
    public static final Integer MAX_SESSION_ID = 65535;
    public static final Integer PACKET_OVERHEAD = 9; // 7 bytes for packet header, 2 for count byte.
        
    @Override
    public ProtocolStack newProtocolStack(ProtocolConfig protocolConfig, SiteAddress siteAddress, ThreadContext astRequester, NetworkAdapter networkAdapter, PropertyBag propertyBag) {
        LookupHelper helper = DependencyResolver.Implementation.sharedInstance().getImplementationOf(LookupHelper.class);
        Integer rcvBufferSize = protocolConfig.valueForPropertyName("MaxPacketSize", 2048);
        SessionManager sessionManager = sessionManager((ZapProtocolConfig)protocolConfig, siteAddress, astRequester, helper);
        PacketLayer packetLayer = new ZapPacketLayer(TraceLogger.L4);
        CompressionLayer compressionLayer = new NoCompressionLayer();
        SecurityLayer securityLayer = securityLayer((ZapProtocolConfig)protocolConfig, siteAddress.getNetworkNumber(), networkAdapter.getRemoteAddress(), helper);
        TransportLayer transportLayer = new NoTransportLayer();
        FramingLayer framingLayer = new CountedByteFramingLayer(TraceLogger.L2, rcvBufferSize);
        return new ProtocolStack(sessionManager, packetLayer, compressionLayer, securityLayer, transportLayer, framingLayer, networkAdapter);
    }
    
    private SecurityLayer securityLayer(ZapProtocolConfig protocolConfig, Integer networkNumber, InetSocketAddress remoteAddress, LookupHelper helper) {
        AuthenticationDelegate delegate = helper.getAuthenticationDelegate();
        Sha1Hasher hasher = new Sha1Hasher(1024);
        ZapAuthenticationStrategy strategy = new ZapAuthenticationStrategy(networkNumber, hasher, delegate, new DefaultSessionKeyGenerator(), remoteAddress, TraceLogger.L3);
        if (protocolConfig.getAuthenticationConfig().getAuthenticationMode() == ZapAuthenticationMode.Promiscuous)
            strategy.enablePromiscuousMode(protocolConfig.getAuthenticationConfig().getMatchNameRe());
        Integer maxPacketSize = protocolConfig.getMaxPacketSize();
        return new DefaultSecurityLayer(strategy, maxPacketSize, TraceLogger.L3);
    }
    
    private SessionManager sessionManager(ZapProtocolConfig protocolConfig, SiteAddress siteAddress, ThreadContext astRequester, LookupHelper helper) {
        FieldTypeLibrary fieldTypeLibrary = helper.getFieldTypeLibrary();
        UnsolicitedMessageHandler unsolicitedMessageHandler = new ZapUnsolicitedMessageHandler();
        SessionlessMessageHandler sessionlessMessageHandler = null; 
        SessionIdGenerator sessionIdGenerator = new DecrementingSessionIdGenerator(MAX_SESSION_ID);
        Integer maxPacketSize = protocolConfig.getMaxPacketSize();
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(maxPacketSize, PACKET_OVERHEAD, maxPacketSize-PACKET_OVERHEAD);
        return new SessionManager(siteAddress, 
                                  unsolicitedMessageHandler, 
                                  sessionlessMessageHandler, 
                                  sessionIdGenerator, 
                                  protocolPacket, 
                                  astRequester, 
                                  new ZapMessageFactory(fieldTypeLibrary), 
                                  TraceLogger.L5);
    }
}
