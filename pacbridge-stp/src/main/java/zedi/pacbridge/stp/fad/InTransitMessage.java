package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

import zedi.pacbridge.utl.CollectionFactory;

class InTransitMessage extends FadMessage {
    private Map<Integer, Segment> segmentMap;
    private int messageId;
    private int sendAttempts;
    private FadMessageStatus messageStatus;
    
    public InTransitMessage(ByteBuffer byteBuffer, int maxPacketSize, int crc, int messageIndex) {
        this(byteBuffer, maxPacketSize, crc, messageIndex, new CollectionFactory());
    }
    
    @SuppressWarnings("unchecked")
    public InTransitMessage(ByteBuffer byteBuffer, int maxPacketSize, int crc, int messageIndex, CollectionFactory collectionFactory) {
        this.segmentMap = collectionFactory.newSynchronizedMap(new TreeMap<Integer, Segment>());
        buildSegments(messageId, byteBuffer, maxPacketSize, crc);
        this.messageStatus = FadMessageStatus.QUEUED;
    }

    int getMessageId() {
        return messageId;
    }
    
    void setMessageId(int messageId) {
        this.messageId = messageId;
        Segment[] segments = segmentMap.values().toArray(new Segment[segmentMap.size()]);
        for (Segment segment : segments)
            segment.setMessageId(messageId);
    }
    
    void setMessageStatus(FadMessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }
    
    public FadMessageStatus getMessageStatus() {
        return messageStatus;
    }
    
    public boolean hasBeenAcknowledged() {
        return messageStatus == FadMessageStatus.ACKNOWLEDGED;
    }
    
    public boolean hasFailed() {
        return messageStatus == FadMessageStatus.FAILED;
    }

    int getSendAttempts() {
        return sendAttempts;
    }
    
    int getNumberOfSegments() {
        return segmentMap.size();
    }

    public FadMessageTracker messageTracker() {
        return new FadMessageTracker(this);
    }
    
    public void resendSegment(int segmentId, FadMessageHandler messageSender) throws IOException {
        sendAttempts++;
        Segment segment = segmentMap.get(segmentId);
        if (segment != null)
            messageSender.handleMessage(segment);
    }

    @Override
    public void transmitThroughMessageTransmitter(FadMessageTransmitter messageTransmitter, ByteBuffer byteBuffer) throws IOException {
        sendAttempts++;
        sendAllSegments(messageTransmitter, byteBuffer);
    }
    
    @Override
    public int size() {
        int maxSize = 0;
        for (Segment segment : segmentMap.values())
            maxSize = Math.max(maxSize, segment.size());
        return maxSize;
    }
    
    @Override
    public boolean isControlMessage() {
        return false;
    }
    
    private void sendAllSegments(FadMessageTransmitter messageTransmitter, ByteBuffer byteBuffer) throws IOException {
        for (Segment segment : segmentMap.values()) {
            segment.transmitThroughMessageTransmitter(messageTransmitter, byteBuffer);
            byteBuffer.clear();
        }
    }
    
    private void buildSegments(int messageId, ByteBuffer byteBuffer, int maxPacketSize, int crc) {
        int segmentId = 0;
        Segment fadSegment = null;
        while (byteBuffer.hasRemaining()) {
            fadSegment = new Segment(maxPacketSize, byteBuffer, messageId, segmentId++);
            segmentMap.put(fadSegment.getSegmentId(), fadSegment);
        }
        if (fadSegment != null)
            fadSegment.setCrc(crc);
    }
}