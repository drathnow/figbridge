package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.crc.Crc16Reflect;
import zedi.pacbridge.utl.crc.CrcCalculator;

class MessageSender {
    private InTransitMessageTracker messageTracker;
    private int maxPacketSize;
    private int maxSegmentsPerMessage;
    private FadMessageFactory inTransitMessageFactory;
    private CrcCalculator crcCalculator;
    private FadMessageHandler messageHandler;
    
    public MessageSender(InTransitMessageTracker messageTracker, FadMessageHandler messageHandler) {
        this(messageTracker, new FadMessageFactory(), new Crc16Reflect(), messageHandler);
    }

    public MessageSender(InTransitMessageTracker messageTracker, FadMessageFactory messageFactory, CrcCalculator crcCalculator, FadMessageHandler messageHandler) {
        setMaxPacketSize(Fad.maxPacketSizeProperty.currentValue());
        setMaxSegmentsPerMessage(Fad.maxSegmentsPerMessageProperty.currentValue());
        this.crcCalculator = crcCalculator;
        this.messageTracker = messageTracker;
        this.inTransitMessageFactory = messageFactory;
        this.messageHandler = messageHandler;
    }

    public void reset() {
        messageTracker.reset();
    }
    
    public void setMaxPacketSize(int maxPacketSize) {
        if (maxPacketSize > Fad.MAX_MAX_PACKET_SIZE)
            throw new IllegalArgumentException("Max packet size cannot be greater than " + Fad.MAX_MAX_PACKET_SIZE);
        this.maxPacketSize = maxPacketSize;
    }
    
    public void setMaxSegmentsPerMessage(int maxSegmentsPerMessage) {
        if (maxSegmentsPerMessage > Fad.MAX_MAX_SEGMENTS)
            throw new IllegalArgumentException("Max segments per message cannot be greater than " + Fad.MAX_MAX_SEGMENTS);
        this.maxSegmentsPerMessage = maxSegmentsPerMessage;
    }

    public FadMessageTracker transmitData(ByteBuffer byteBuffer) throws IOException {
        int crc = crcCalculator.calculate(Fad.CRC_SEED, byteBuffer.slice());
        InTransitMessage inTransitMessage = inTransitMessageFactory.newInTransitMessage(byteBuffer, maxPacketSize, crc);
        if (inTransitMessage.getNumberOfSegments() > maxSegmentsPerMessage)
            throw new IOException("Resulting FAD message exceeds max segments");
        messageTracker.sendAndTrackInTransitMessage(inTransitMessage, messageHandler);
        return inTransitMessage.messageTracker();
    }

    public void transmitControlMessage(ControlMessage controlMessage) throws IOException {
        messageHandler.handleMessage(controlMessage);
    }

    public boolean isIdle() {
        return getInTransitMessagesCount() == 0;
    }

    public int getInTransitMessagesCount() {
        return messageTracker.getInTransitMessagesCount();
    }

    public void close() {
        messageTracker.reset();
    }

    public int getQueuedMessageCount() {
        return  messageTracker.getQueuedMessageCount();
    }

    public void handleResendRequestForMessageWithMessageId(int messageId) {
        messageTracker.handleResendRequestForMessageWithMessageId(messageId, messageHandler);
    }
}