package zedi.figdevice.emulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figdevice.emulator.net.AuthenticationListener;
import zedi.figdevice.emulator.net.FigProtocolStack;
import zedi.figdevice.emulator.net.ProtocolStackFactory;
import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.pacbridge.net.TcpNetworkAdapter;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.DispatcherRequest;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.utl.io.Sha1Hasher;
import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.reporting.ZapReport;

public class FigDevice implements AuthenticationListener, Comparable<FigDevice> {
    private static final Logger logger = LoggerFactory.getLogger(FigDevice.class.getName());
    public static final String REPORT_SENT_NOTIFICATION = "figDevice.deviceConnected";
    
    private FigProtocolStack protocolStack;
    private String username;
    private NotificationCenter notificationCenter;
    private InetSocketAddress bridgeAddress;
    private ThreadContext threadContext;
    private BundledReportMessageGenerator reportMessageGenerator;
    private Integer sequenceNumber;
    private LoggingContext loggingContext;
    private Integer reconnectIntervalSeconds;
    private Long nextReconnectTime;
    private long totalReportsSent;
    private FutureTimer future;
    private boolean stopped;
    
    public FigDevice(String username, 
                     NotificationCenter notificationCenter, 
                     InetSocketAddress bridgeAddress, 
                     BundledReportMessageGenerator reportMessageGenerator, 
                     Integer reconnectIntervalSeconds) {
        this.username = username;
        this.notificationCenter = notificationCenter;
        this.bridgeAddress = bridgeAddress;
        this.reportMessageGenerator = reportMessageGenerator;
        this.sequenceNumber = 1;
        this.reconnectIntervalSeconds = reconnectIntervalSeconds;
        this.loggingContext = new LoggingContext(new NuidSiteAddress(username, 0));
        this.totalReportsSent = 0L;
        this.nextReconnectTime = 0L;
        this.future = null;
    }

    public FigDevice(String username, 
            NotificationCenter notificationCenter, 
            InetSocketAddress bridgeAddress, 
            BundledReportMessageGenerator reportMessageGenerator) {
        this(username, notificationCenter, bridgeAddress, reportMessageGenerator, 0);
    }

    public String getUsername() {
        return username;
    }
    
    public long getTotalReportsSent() {
        return totalReportsSent;
    }
    
    public boolean isStopped() {
        return stopped;
    }
    
    public void stop() {
        if (threadContext != null) {
            threadContext.requestTrap(new ThreadContextHandler() {
                @Override
                public void handleSyncTrap() {
                    stopInContext();
                }
            });
        }
    }
    
    public void start(final NetworkEventDispatcherManager manager) {
        this.stopped = false;
        if (false == stopped)
            manager.queueDispatcherRequest(new ConnectionRequest());
    }
    
    @Override
    public void authenticate() {
        logger.info("Connected");
        future = queueNextReportTrap();
        if (reconnectIntervalSeconds > 0)
            nextReconnectTime = System.currentTimeMillis() + reconnectIntervalSeconds*1000L;
    }
    

    @Override
    public int compareTo(FigDevice otherDevice) {
        return username.compareTo(otherDevice.username);
    }

    private void sendReportOrRecycleConnection() throws IOException {
        loggingContext.setupContext();
        if (nextReconnectTime > 0  && System.currentTimeMillis() > nextReconnectTime) {
            logger.info("Disconnecting");
            protocolStack.disconnect();
            try {
                logger.info("Connecting...");
                protocolStack.connect();
            } catch (Exception e) {
                logger.error("Unable to reconnect to bridge", e);
            }
        } else {
            if (sequenceNumber == 65535)
                sequenceNumber = 1;
            BundledReportMessage reportMessage = reportMessageGenerator.nextBundledReportMessage(sequenceNumber++);
            for (ZapReport report : reportMessage.reportsMap().values()) {
                Long eventId = report.getEventId();
                notificationCenter.postNotification(REPORT_SENT_NOTIFICATION, new PublishedReportAttachement(username, eventId));
                totalReportsSent++;
            }
            protocolStack.sendUnsolicited(reportMessage);
            future = queueNextReportTrap();
        }
        loggingContext.clearContext();
    }
    
    private FutureTimer queueNextReportTrap() {
        if (reportMessageGenerator.isFinished() == false) {
            long nextReportTime = reportMessageGenerator.secondsUntilNextReportIsDue().longValue();
            logger.debug("Waiting " + nextReportTime + " seconds before sending next report.");
            return threadContext.requestTrap(new ThreadContextHandler() {
                @Override
                public void handleSyncTrap() {
                    FigDevice.this.loggingContext.setupContext();
                    try {
                        sendReportOrRecycleConnection();
                    } catch (IOException e) {
                        logger.warn("Unable to send message because connection is closed. Emulator has stopped");
                    }
                    FigDevice.this.loggingContext.clearContext();
                }
            }, nextReportTime, TimeUnit.SECONDS);
        }
        return null;
    }
    
    private void stopInContext() {
        loggingContext.setupContext();
        if (future != null)
            future.cancel();
        future = null;
        logger.info("Stopping");
        protocolStack.disconnect();
        loggingContext.clearContext();
        stopped = true;
    }
        
    class ConnectionRequest implements DispatcherRequest {

        @Override
        public void handleRequest(DispatcherKey dispatcherKey, ThreadContext threadContext) {
            FigDevice.this.threadContext = threadContext;
            ProtocolStackFactory factory = new ProtocolStackFactory();
            try {
                SocketChannelWrapper channel = new SocketChannelWrapper(SocketChannel.open());
                dispatcherKey.registerChannel(channel);
                NuidSiteAddress siteAddress = new NuidSiteAddress(username, 0);
                TcpNetworkAdapter adapter = new TcpNetworkAdapter(bridgeAddress, siteAddress, dispatcherKey, TraceLogger.L1);
                protocolStack = factory.newProtocolStack(adapter, username, threadContext, new Sha1Hasher(1024), FigDevice.this);
                protocolStack.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}