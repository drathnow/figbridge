package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.ProtocolPacket;
import zedi.pacbridge.net.SecurityLayer;
import zedi.pacbridge.net.TransportLayer;
import zedi.pacbridge.net.annotations.AsyncRequester;
import zedi.pacbridge.net.annotations.ProtocolLayer;
import zedi.pacbridge.utl.DefaultInactivityStrategy;
import zedi.pacbridge.utl.InactivityStrategy;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.utl.strategies.DefaultRetryStrategy;
import zedi.pacbridge.utl.strategies.RetryStrategy;


/**
 * 
 * Fragment Assembler/Disassembler.
 * 
 * FAD divides a message into multiple segments where the max length of any
 * segment is defined is defined by the STP max packet size. The length of a
 * segment is calculated by first determining the number of bytes required to
 * hold the payload, which requires a pass of the data to determine if any of
 * the data contains control characters used by APL. If it does, these
 * characters need to be escaped.
 * 
 */
@ProtocolLayer(name="FAD")
public class Fad implements TransportLayer, ThreadContextHandler {

    private static Logger logger = LoggerFactory.getLogger(Fad.class);
    
    static final int CRC_SEED = 0xFFFF;
    
    /**
     * Defines the maximum number of packets a message can be broken up into.
     */
    public static final String MAX_SEGMENTS_PROPERTY_NAME = "fad.maxSegmentsPerMessage";
    public static final int DEFAULT_MAX_SEGMENTS = 64;
    public static final int MIN_MAX_SEGMENTS = 1;
    public static final int MAX_MAX_SEGMENTS = 64;

    /**
     * Defines the maximum packet size (segment length + overhead);
     */
    public static final String MAX_PACKET_SIZE_PROPERTY_NAME = "fad.maxPacketSize";
    public static final int DEFAULT_MAX_PACKET_SIZE = 1024;
    public static final int MIN_MAX_PACKET_SIZE = 2;
    public static final int MAX_MAX_PACKET_SIZE = 65534;

    /**
     * Defines the number of seconds to wait for a response (ACK) before resending a message
     */
    public static final String TRANSMIT_TIMEOUT_PROPERTY_NAME = "fad.transmitTimeoutSeconds";
    public static final int DEFAULT_TRANSMIT_TIMEOUT = 60;
    public static final int MIN_TRANSMIT_TIMEOUT = 2;
    
    /**
     * Defines the number of seconds to wait for all segments of a multi-segment message to
     * arrive before the message is discarded.
     */
    public static final String RECEIVE_TIMEOUT_PROPERTY_NAME = "fad.receiveTimeoutSeconds";
    public static final int DEFAULT_RECEIVE_TIMEOUT = 120;
    public static final int MIN_RECEIVE_TIMEOUT = 2;
    
    /**
     * Defines the maximum number of times a message will be resent before it is dropped.
     */
    public static final String MAX_RETRIES_PROPERTY_NAME = "fad.maxRetries";
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final int MIN_MAX_RETRIES = 0;
    
    static IntegerSystemProperty maxSegmentsPerMessageProperty = new IntegerSystemProperty(MAX_SEGMENTS_PROPERTY_NAME, DEFAULT_MAX_SEGMENTS, MIN_MAX_SEGMENTS, MAX_MAX_SEGMENTS);
    static IntegerSystemProperty transmitTimeoutProperty = new IntegerSystemProperty(TRANSMIT_TIMEOUT_PROPERTY_NAME, DEFAULT_TRANSMIT_TIMEOUT, MIN_TRANSMIT_TIMEOUT);
    static IntegerSystemProperty receiveTimeoutProperty = new IntegerSystemProperty(RECEIVE_TIMEOUT_PROPERTY_NAME, DEFAULT_RECEIVE_TIMEOUT, MIN_RECEIVE_TIMEOUT);
    static IntegerSystemProperty maxPacketSizeProperty = new IntegerSystemProperty(MAX_PACKET_SIZE_PROPERTY_NAME, DEFAULT_MAX_PACKET_SIZE, MIN_MAX_PACKET_SIZE);
    static IntegerSystemProperty maxRetriesProperty = new IntegerSystemProperty(MAX_RETRIES_PROPERTY_NAME, DEFAULT_MAX_RETRIES, MIN_MAX_RETRIES);
    
    private LinkedList<Integer> retransmitMessageQueue;
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
    private MessageDeserializer messageDeserializer;
    private ByteBuffer transmitByteBuffer;
    private FadMessageTransmitter messageTransmitter;
    private RetransmitEventHandler retransmitEventHandler;
    private FramingLayer framingLayer;
    private SecurityLayer securityLayer;
    private ThreadContext astRequester;
    private FadMessageTracker lastMessageTracker;

    public Fad() {
        this(new DefaultInactivityStrategy(transmitTimeoutProperty.currentValue()), 
                new DefaultInactivityStrategy(receiveTimeoutProperty.currentValue()),
                new DefaultRetryStrategy(maxRetriesProperty.currentValue()));
    }
    
    public Fad(InactivityStrategy inTransitInactivityStrategy, 
                InactivityStrategy pendingMessageInactivityStrategy, 
                RetryStrategy retryStrategy) {
        retransmitEventHandler = new RetransmitEventHandler() {
            @Override
            public void retransmitMessageWithMessageId(int messageId) {
                synchronized (retransmitMessageQueue) {
                    retransmitMessageQueue.addLast(messageId);
                }
                Fad.this.astRequester.requestTrap(Fad.this);
            }
        };
        
        FadDataHandler dataHandler = new FadDataHandler() {

            @Override
            public void handleData(ByteBuffer byteBuffer) throws ProtocolException {
                securityLayer.handleReceivedData(byteBuffer);
            }
        };
        
        FadMessageHandler fadMessageHandler = new FadMessageHandler() {
            @Override
            public void handleMessage(FadMessage message) throws IOException {
                if (message.size() > transmitByteBuffer.capacity())
                    transmitByteBuffer = byteBufferWithSize(message.size());
                message.transmitThroughMessageTransmitter(messageTransmitter, transmitByteBuffer);
                transmitByteBuffer.clear();
            }
        };
        
        MaxRetriesResendMessageStrategy maxRetriesResendMessageStrategy = new MaxRetriesResendMessageStrategy(retryStrategy);
        InTransitMessageTracker inTransitMessageTracker = new InTransitMessageTracker(inTransitInactivityStrategy, maxRetriesResendMessageStrategy, new MessageWindow(FadHeader.MAX_MESSAGE_ID+1), retransmitEventHandler);
        PendingMessageTracker pendingMessageTracker = new PendingMessageTracker(pendingMessageInactivityStrategy);
        this.messageTransmitter = new MessageTransmitter();
        this.messageSender = new MessageSender(inTransitMessageTracker, fadMessageHandler);
        this.messageReceiver = new MessageReceiver(pendingMessageTracker, inTransitMessageTracker, dataHandler, fadMessageHandler);
        this.transmitByteBuffer = byteBufferWithSize(maxPacketSizeProperty.currentValue());
        this.retransmitMessageQueue = new LinkedList<Integer>();
        saveStuff(new MessageDeserializer(), messageReceiver, messageSender);
    }
    
    Fad(MessageDeserializer messageDeserializer, 
            MessageReceiver messageReceiver, 
            MessageSender messageSender) {
        this.retransmitEventHandler = new RetransmitEventHandler() {
            @Override
            public void retransmitMessageWithMessageId(int messageId) {
                synchronized (retransmitMessageQueue) {
                    retransmitMessageQueue.addLast(messageId);
                }
                Fad.this.astRequester.requestTrap(Fad.this);
            }
        };
        this.transmitByteBuffer = byteBufferWithSize(maxPacketSizeProperty.currentValue());
        this.retransmitMessageQueue = new LinkedList<Integer>();
        saveStuff(messageDeserializer, messageReceiver, messageSender);
    }

    @AsyncRequester
    public void setAstRequester(ThreadContext astRequester) {
        this.astRequester = astRequester;
    }
    
    @Override
    public boolean isActive() {
        return (messageSender.isIdle() && messageReceiver.isIdle()) == false;
    }
    
    @Override
    public void close() {
        messageSender.close();
        messageReceiver.close();
        framingLayer.close();
        reset();
    }
    
    public boolean supportsMessageTracking() {
        return true;
    }
    
    public void setTransmitTimeoutSeconds(Integer timeoutSeconds) {
        messageReceiver.setTransmitTimeoutSeconds(timeoutSeconds);
    }
    
    public void setReceiveTimeoutSeconds(Integer timeoutSeconds) {
        messageReceiver.setReceiveTimeoutSeconds(timeoutSeconds);
    }
    
    public void setMaxPacketSize(int maxPacketSize) {
        messageSender.setMaxPacketSize(maxPacketSize);
    }

    public void setMaxSegmentsPerMessage(int maxSegmentsPerMessage) {
        messageSender.setMaxSegmentsPerMessage(maxSegmentsPerMessage);
    }

    public int getPendingMessagesCount() {
        return messageReceiver.getPendingMessagesCount();
    }

    public int getInTransitMessagesCount() {
        return messageSender.getInTransitMessagesCount();
    }

    public int getQueuedMessageCount() {
        return messageSender.getQueuedMessageCount();
    }
    
    
    void transmitData(ByteBuffer byteBuffer) throws IOException {
        lastMessageTracker = messageSender.transmitData(byteBuffer);
    }
    
    public FadMessageTracker lastMessageTracker() {
        return lastMessageTracker;
    }

    void handleReceivedData(ByteBuffer byteBuffer) throws ProtocolException {
        try {
            FadMessage fadMessage = messageDeserializer.fadMessageFromByteBuffer(byteBuffer);
            if (fadMessage.isControlMessage())
                messageReceiver.handleControlMessage((ControlMessage)fadMessage);
            else
                messageReceiver.handleSegmentMessage((Segment)fadMessage);
        } catch (Exception e) {
            logger.error("Unable to decode message", e);
        }
    }
    
    @Override
    public void reset() {
        messageReceiver.reset();
        messageSender.reset();
        lastMessageTracker = null;
        if (transmitByteBuffer.capacity() > maxPacketSizeProperty.currentValue())
            transmitByteBuffer = byteBufferWithSize( maxPacketSizeProperty.currentValue());
    }
    
    private void saveStuff(MessageDeserializer messageDeserializer, MessageReceiver messageReceiver, MessageSender messageSender) {
        this.messageSender = messageSender;
        this.messageReceiver = messageReceiver;
        this.messageDeserializer = messageDeserializer;
    }

    private static ByteBuffer byteBufferWithSize(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void handleSyncTrap() {
        LinkedList<Integer> myList;
        synchronized (retransmitMessageQueue) {
            myList = new LinkedList<Integer>(retransmitMessageQueue);
            retransmitMessageQueue.clear();
        }
        for (Integer messageId : myList)
            messageSender.handleResendRequestForMessageWithMessageId(messageId);
    }
    
    // For testing only
    RetransmitEventHandler getRetransmitEventHandler() {
        return retransmitEventHandler;
    }
    
    // For testing only
    LinkedList<Integer> getRetransmitMessageQueue() {
        return retransmitMessageQueue;
    }

    
    private class MessageTransmitter implements FadMessageTransmitter {

        @Override
        public void transmitByteBuffer(ByteBuffer byteBuffer) throws IOException {
            framingLayer.transmitData(byteBuffer);
        }
    }


    @Override
    public void setSecurityLayer(SecurityLayer securityLayer) {
        this.securityLayer = securityLayer;
    }

    @Override
    public void setFramingLayer(FramingLayer framingLayer) {
        this.framingLayer = framingLayer;
    }

    @Override
    public void receive(ProtocolPacket protocolPacket) throws ProtocolException {
        handleReceivedData(protocolPacket.bodyByteBuffer());
    }

    @Override
    public void transmit(ProtocolPacket protocolPacket) throws IOException {
        transmitData(protocolPacket.bodyByteBuffer());
    }

    @Override
    public void start() throws IOException {
    }

}
