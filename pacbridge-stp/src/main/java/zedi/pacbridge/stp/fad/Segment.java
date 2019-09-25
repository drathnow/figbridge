package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

class Segment extends FadMessage {

    private FadHeader header;
    private byte[] payload;
    private int sendCount;
    private long sendTime;

    public Segment(int maxSegmentSize, ByteBuffer byteBuffer, int messageId, int segmentId) {
        int segmentSize = maxSegmentSize == 0 ? byteBuffer.remaining() : Math.min(maxSegmentSize, byteBuffer.remaining());
        this.payload = new byte[segmentSize];
        byteBuffer.get(payload);
        this.header = new FadHeader();
        this.header.setAsSegmentHeaderWithoutCrc(messageId, segmentId);
        this.sendCount = 0;
    }
    
    private Segment(FadHeader fadHeader, ByteBuffer byteBuffer) {
        this.header = fadHeader;
        this.payload = new byte[byteBuffer.remaining()];
        byteBuffer.get(payload);
    }

    @Override
    public boolean isControlMessage() {
        return header.isControlHeader();
    }

    public boolean hasCrc() {
        return header.hasCrc();
    }
    
    public int getCrc() {
        return header.getCrc();
    }

    public void setCrc(int crc) {
        header.setAsSegmentHeaderWithCrc(header.getMessageId(), header.getSegmentId(), crc);
    }

    public void setMessageId(int messageId) {
        header.setMessageId(messageId);
    }
    
    public int getMessageId() {
        return header.getMessageId();
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isLastSegment() {
        return header.isLastSegment();
    }

    public boolean isResponseRequired() {
        return header.isAcknowledgementRequired();
    }

    public int getSegmentId() {
        return header.getSegmentId();
    }

    public boolean isAcknowledgement() {
        return header.isAcknowledgement();
    }

    public boolean isAcknowledgementRequired() {
        return header.isAcknowledgementRequired();
    }

    public int getSendCount() {
        return sendCount;
    }
    
    public long getSendTime() {
        return sendTime;
    }

    public boolean isResendRequest() {
        return header.isResendRequest();
    }

    @Override
    public int size() {
        return FadHeader.FAD_MAX_SIZE + payload.length;
    }
    
    @Override
    public void transmitThroughMessageTransmitter(FadMessageTransmitter messageTransmitter, ByteBuffer byteBuffer) throws IOException {
        header.serialize(byteBuffer);
        byteBuffer.put(payload);
        byteBuffer.flip();
        messageTransmitter.transmitByteBuffer(byteBuffer);
        sendCount++;
        sendTime = System.currentTimeMillis();
    }
    
    public static Segment fadSegmentWithHeaderAndBufferPayload(FadHeader fadHeader, ByteBuffer payloadByteBuffer) {
        return new Segment(fadHeader, payloadByteBuffer);
    }
}