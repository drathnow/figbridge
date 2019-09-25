package zedi.figdevice.emulator.net;

import java.util.Map;
import java.util.TreeMap;

import zedi.figdevice.emulator.InjectModel;
import zedi.figdevice.emulator.utl.MessageTracker;
import zedi.pacbridge.net.CompressionLayer;
import zedi.pacbridge.net.CountedByteFramingLayer;
import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.NoCompressionLayer;
import zedi.pacbridge.net.NoTransportLayer;
import zedi.pacbridge.net.PacketLayer;
import zedi.pacbridge.net.SecurityLayer;
import zedi.pacbridge.net.TcpNetworkAdapter;
import zedi.pacbridge.net.TransportLayer;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.ZapPacketLayer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ProtocolStackFactory {
    public static final Integer DEFAULT_TIMEOUT_SECONDS = 10;
    
    public FigProtocolStack newProtocolStack(TcpNetworkAdapter networkAdapter, String username, ThreadContext threadContext, Hasher hasher, AuthenticationListener listener) {
        final Injector injector = Guice.createInjector(new InjectModel());
        Map<Integer, MessageHandler> handlerMap = new TreeMap<Integer, MessageHandler>();
        NotificationCenter notificationCenter = injector.getInstance(NotificationCenter.class);
        GlobalScheduledExecutor executor = injector.getInstance(GlobalScheduledExecutor.class);
        MessageTracker messageTracker = new MessageTracker(DEFAULT_TIMEOUT_SECONDS, executor, notificationCenter);
        FigSessionManager sessionLayer = new FigSessionManager(threadContext, handlerMap, messageTracker);
        PacketLayer packetLayer = new ZapPacketLayer(TraceLogger.L3);
        CompressionLayer compressionLayer = new NoCompressionLayer(); 
        SecurityLayer securityLayer = new FigAuthenticationLayer(new AuthenticationStrategy(hasher, username), listener);
        TransportLayer transportLayer  = new NoTransportLayer();
        FramingLayer framingLayer = new CountedByteFramingLayer(TraceLogger.L2); 
        return new FigProtocolStack(sessionLayer, packetLayer, compressionLayer, securityLayer, transportLayer, framingLayer, networkAdapter);
    }
}
