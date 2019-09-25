package zedi.pacbridge.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.DefaultTrafficLogger;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.net.logging.TrafficLogger;
import zedi.pacbridge.utl.ActivityTracker;
import zedi.pacbridge.utl.DefaultActivityTracker;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SiteAddress;

public class TcpNetworkAdapter implements NetworkAdapter, ReadEventHandler, ConnectEventHandler {
    public static final String TCP_RCV_BUFFER_SIZE_PROPERTY_NAME = "tcpNetwork.rcvBufferSize";
    public static final Integer DEFAULT_TCP_RCV_BUFFER_SIZE = 1024;
    public static final Integer MIN_TCP_RCV_BUFFER_SIZE = 512;
    
    private static final Logger logger = LoggerFactory.getLogger(TcpNetworkAdapter.class.getName());
    private static final IntegerSystemProperty rcvBufferSizeProperty = new IntegerSystemProperty(TCP_RCV_BUFFER_SIZE_PROPERTY_NAME, DEFAULT_TCP_RCV_BUFFER_SIZE, MIN_TCP_RCV_BUFFER_SIZE);

    enum State {Connected, Disconnected}

    private FramingLayer framingLayer;
    private State currentState;
    private byte[] rcvBuffer;
    private ByteBuffer rcvByteBuffer;
    private SocketChannelWrapper channelWrapper;
    private ActivityTracker activityTracker;
    private DispatcherKey dispatcherKey;
    private NetworkAdapterListener eventListener;
    private TrafficLogger trafficLogger;
    private SiteAddress siteAddress;
    private LoggingContext loggingContext;
    private TraceLogger traceLogger;
    private InetSocketAddress remoteAddress;
    private int bytesTransmitted;
    private int bytesReceived;

    private TcpNetworkAdapter(DispatcherKey dispatcherKey, Integer rcvBufferSize, TraceLogger traceLogger) throws IOException {
        this.rcvBuffer = new byte[rcvBufferSize];
        this.rcvByteBuffer = ByteBuffer.wrap(this.rcvBuffer);
        this.dispatcherKey = dispatcherKey;
        this.traceLogger = traceLogger;
        this.eventListener = null;
        this.activityTracker = new DefaultActivityTracker();
        this.trafficLogger = new DefaultTrafficLogger(logger);
        this.bytesTransmitted = 0;
        this.bytesReceived = 0;
    }
    
    public TcpNetworkAdapter(SiteAddress siteAddress, SocketChannelWrapper connectedChannel, DispatcherKey dispatcherKey, TraceLogger traceLogger) throws IOException {
        this(dispatcherKey, rcvBufferSizeProperty.currentValue(), traceLogger);
        this.channelWrapper = connectedChannel;
        this.remoteAddress = channelWrapper.remoteAddress();
        this.currentState = State.Connected;
        this.dispatcherKey.attach(channelWrapper, this);
        setSiteAddress(siteAddress);
    }

    public TcpNetworkAdapter(InetSocketAddress remoteAddress, SiteAddress siteAddress, DispatcherKey dispatcherKey, TraceLogger traceLogger) throws IOException {
        this(dispatcherKey, rcvBufferSizeProperty.currentValue(), traceLogger);
        this.currentState = State.Disconnected;
        this.remoteAddress = remoteAddress;
        setSiteAddress(siteAddress);
    }
    
    @Override
    public void setSiteAddress(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
        this.loggingContext = new LoggingContext(siteAddress);
    }
    
    @Override
    public LoggingContext loggingContext() {
        return loggingContext;
    }
    
    @Override
    public void setNetworkAdapterListener(NetworkAdapterListener listener) {
        this.eventListener = listener;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    @Override
    public int getBytesReceived() {
        return bytesReceived;
    }
    
    @Override
    public int getBytesTransmitted() {
        return bytesTransmitted;
    }
    
    @Override
    public long getLastActivityTime() {
        return activityTracker.getLastActivityTime();
    }
    @Override
    public void start() throws IOException {
        switch (currentState) {
            case Connected :
                if (channelWrapper != null)
                    dispatcherKey.addChannelInterest(channelWrapper, SelectionKey.OP_READ);
                break;
            case Disconnected :
                connect();
                break;
        }
    }

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }
    
    @Override
    public void handleConnect() {
        try {
            bytesTransmitted = 0;
            bytesReceived = 0;
            this.channelWrapper.finishConnect();
            activityTracker.update();
            dispatcherKey.addChannelInterest(channelWrapper, SelectionKey.OP_READ);
            currentState = State.Connected;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void handleRead() {
        switch (currentState) {
            case Connected :
                activityTracker.update();
                int bytesRead = 0;
                int position = rcvByteBuffer.position();
                try {
                    bytesRead = channelWrapper.read(rcvByteBuffer);
                } catch (IOException e) {
                    closeConnectionWithExceptionDuringOperation(e, "read");
                    return;
                }
                if (bytesRead == -1) {
                    logger.info("Connection closed by remote device.");
                    closeCurrentSocket();
                    // At this point, we just tell the event listener (if there is one) that the
                    // socket closed nicely. 
                    if (eventListener != null)
                        eventListener.handleCloseEvent();
                } else {
                    try {
                        trafficLogger.logIncomingData(rcvByteBuffer.array(), position, bytesRead);
                        bytesReceived += bytesRead;
                        rcvByteBuffer.flip();
                        framingLayer.receive(rcvByteBuffer);
                    } catch (Exception e) {
                        logger.error("Unable to handle incoming data", e);
                    } finally {
                        //
                        // It's possible that, while handling this read event, this adapater was closed by a reentrant call.
                        // If this happens, the channel wrapper will be null
                        //
                        if (channelWrapper != null) {
                            try {
                                dispatcherKey.addChannelInterest(channelWrapper, SelectionKey.OP_READ);
                            } catch (IOException e) {
                                logger.error("Unable to register read intertest on channel.  Channel closed", e);
                                closeConnectionWithExceptionDuringOperation(e, "framingLayer.receive");
                            }
                        }
                    }
                }
                break;
                
            case Disconnected :
                break;
        }
    }
    
    private void closeCurrentSocket() {
        if (traceLogger.isEnabled())
            traceLogger.trace("Closing socket");
        try {
            dispatcherKey.forgetChannel(channelWrapper);
        } catch (IOException eatIt) {
            logger.debug("Unexpected exception during forgetChannel", eatIt);
        }
        try {
            channelWrapper.close();
        } catch (IOException eatIt) {
            logger.debug("Unexpected exception during SocketChannel.close()", eatIt);
        }
        currentState = State.Disconnected;
        channelWrapper = null;
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException {
        Selector privateSelector = null;
        SelectionKey privateKey = null;
        switch (currentState) {
            case Connected : {
                activityTracker.update();
                ByteBuffer byteBuffer = protocolPacket.bodyByteBuffer();
                try {
                    transmitAndLog(byteBuffer);
                    if (byteBuffer.hasRemaining()) {
                        privateSelector = Selector.open();
                        privateKey = channelWrapper.getChannel().register(privateSelector, SelectionKey.OP_WRITE);
                        while (privateSelector.select() != 1 && byteBuffer.hasRemaining())
                            transmitAndLog(byteBuffer);
                    }
                    
                } catch (IOException e) {
                    closeConnectionWithExceptionDuringOperation(e, "write");
                } finally {
                    if (privateKey != null)
                        privateKey.cancel();
                    if (privateSelector != null)
                        privateSelector.close();
                }
            }
            break;
            
            case Disconnected :
                throw new ClosedChannelException();
        }
    }

    /**
     * This method will NOT generate a close event.
     */
    @Override
    public void close() {
        switch (currentState) {
            case Connected : 
                closeCurrentSocket();
                reset();
                break;
            case Disconnected:
                reset();
                break;
        }
    }
    
    private void transmitAndLog(ByteBuffer byteBuffer) throws IOException {
        int position = byteBuffer.position();
        int bytesSent = channelWrapper.write(byteBuffer);
        if (bytesSent > 0) {
            bytesTransmitted += bytesSent;
            trafficLogger.logOutgoingData(byteBuffer.array(), position, bytesSent);
        }
    }
    
    private void closeConnectionWithExceptionDuringOperation(Exception e, String operation) {
        closeCurrentSocket();
        StringBuilder stringBuilder = new StringBuilder();
        if (isForcedDisconnectException(e)) {
            stringBuilder.append("Connection closed unexpectedly! Cause: ")
                            .append(e.toString());
            logger.error(stringBuilder.toString());
        } else {
            stringBuilder.append("Unexpected IOException during ")
            .append(operation)
            .append(". Closing connection - ");
            logger.error(stringBuilder.toString(), e);
        }
        notifyEventListener(e, operation);
    }
    
    private void notifyEventListener(Exception exception, String operation) {
        StringBuilder stringBuilder = new StringBuilder();
        if (eventListener != null) {
            stringBuilder.setLength(0);
            stringBuilder.append(siteAddress.toString())
                         .append(" - ")
                         .append(operation);
            eventListener.handleUnexpectedCloseEvent(exception, siteAddress, remoteAddress, stringBuilder.toString());
        }
    }
    
    private void connect() throws IOException {
        if (this.channelWrapper != null)
            closeCurrentSocket();
        this.channelWrapper = new SocketChannelWrapper(SocketChannel.open());
        dispatcherKey.registerChannel(channelWrapper);
        dispatcherKey.attach(channelWrapper, this);
        dispatcherKey.addChannelInterest(channelWrapper, SelectionKey.OP_CONNECT);
        this.channelWrapper.connect(remoteAddress);
    }
        
    private boolean isForcedDisconnectException(Exception exception) {
        return exception.getMessage() != null && exception.getMessage().contains("An existing connection was forcibly closed by the remote host");
    }

    @Override
    public void reset() {
        rcvByteBuffer.clear();
    }

}
