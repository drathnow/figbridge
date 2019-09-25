package zedi.figdevice.emulator.net;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figdevice.emulator.utl.MessageTracker;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.PacketLayer;
import zedi.pacbridge.net.ReceiveProtocolPacket;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.net.SessionLayer;
import zedi.pacbridge.net.TransmitProtocolPacket;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.zap.ZapMessageType;

public class FigSessionManager implements SessionLayer {
    private static final Logger logger = LoggerFactory.getLogger(FigSessionManager.class.getName());
    private static final int HEADER_SIZE = 10;

    public static final String MAX_PACKET_SIZE_PROPERTY_NAME = "figdevice.maxPacketSize";
    public static final Integer DEFAULT_MAX_PACKET_SIZE = 1024;
    public static final Integer MIN_MAX_PACKET_SIZE = 1024;
    
    public static final String RCV_BUFFER_INCREMENT_SIZE_PROPERTY_NAME = "figdevice.rcvIncrementSize";
    public static final Integer DEFAULT_RCV_BUFFER_INCREMENT_SIZE = 500;
    public static final Integer MIN_RCV_BUFFER_INCREMENT_SIZE = 100;

    private static IntegerSystemProperty maxPacketSize = new IntegerSystemProperty(MAX_PACKET_SIZE_PROPERTY_NAME, DEFAULT_MAX_PACKET_SIZE, MIN_MAX_PACKET_SIZE); 
    private static IntegerSystemProperty rcvBufferIncrementSize = new IntegerSystemProperty(RCV_BUFFER_INCREMENT_SIZE_PROPERTY_NAME, DEFAULT_RCV_BUFFER_INCREMENT_SIZE, MIN_RCV_BUFFER_INCREMENT_SIZE); 
    
    private ThreadContext threadContext;
    private ThreadContextHandler contextHandler;
    private Lock queueLock;
    private LinkedList<AsyncCommand> commandQueue;
    private Map<Integer, MessageHandler> handlerMap;
    private PacketLayer packetLayer;
    private TransmitProtocolPacket protocolPacket;
    private MessageTracker messageTracker;

    public FigSessionManager(ThreadContext threadContext, Map<Integer, MessageHandler> handlerMap, MessageTracker messageTracker) {
        this.threadContext = threadContext;
        this.handlerMap = handlerMap;
        this.protocolPacket = new TransmitProtocolPacket(maxPacketSize.currentValue(), HEADER_SIZE, maxPacketSize.currentValue()-HEADER_SIZE);
        this.messageTracker = messageTracker;
        this.contextHandler = new ThreadContextHandler() {
            @Override
            public void handleSyncTrap() {
                FigSessionManager.this.queueLock.lock();
                try {
                    if (FigSessionManager.this.commandQueue.isEmpty() == false)
                        FigSessionManager.this.commandQueue.removeFirst().execute();
                } finally {
                    FigSessionManager.this.queueLock.unlock();
                }
            }
        };
    }
    
    public boolean isActive() {
        return messageTracker.hasOutstandingAcks();
    }
    
    public void sendUnsolicited(Message message) throws IOException {
        messageTracker.trackMessage(message.sequenceNumber());
        privateSend(message);
    }

    @Override
    public Session newSession() {
        return null;
    }

    @Override
    public void setPacketLayer(PacketLayer packetLayer) {
        this.packetLayer = packetLayer;
    }

    @Override
    public void receive(ReceiveProtocolPacket protocolPacket, Integer messageNumber, Integer sequenceNumber, Integer sessionId) {
        ZapMessageType messageType = ZapMessageType.messageTypeForNumber(messageNumber);
        if (messageType.equals(ZapMessageType.Acknowledgement)) {
            messageTracker.stopTrackingContainerWithSequenceNumber(sequenceNumber);
        } else {
            logger.error("Message type not currently supported: " + messageType.getName() + ". Message discarded.");
        }
    }

    @Override
    public void start() throws IOException {
        packetLayer.start();
    }

    @Override
    public void close() {
        packetLayer.close();
    }

    @Override
    public void reset() {
        packetLayer.reset();
    }

    @Override
    public void setSiteAddress(SiteAddress siteAddress) {
        new NuidSiteAddress("FixDevice", 0);
    }
    
    private void privateSend(Message message) throws IOException {
        protocolPacket.reset();
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        try {
            message.serialize(bodyByteBuffer);
            protocolPacket.setBodyLength(bodyByteBuffer.position());
            protocolPacket.merge();
            packetLayer.transmit(protocolPacket, message.messageType().getNumber(), message.sequenceNumber(), 0);
        } catch (BufferOverflowException e) {
            int newSize = protocolPacket.packetSize() + rcvBufferIncrementSize.currentValue();
            protocolPacket = new TransmitProtocolPacket(newSize, HEADER_SIZE, newSize-HEADER_SIZE);
            privateSend(message);
        }
    }
    
}
