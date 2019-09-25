package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.crc.Crc16Reflect;
import zedi.pacbridge.utl.crc.CrcCalculator;
import zedi.pacbridge.utl.crc.CrcException;


/**
 * FAD headers are either 2 or 4 bytes in length: 2 bytes if the header is for a multi fragment
 * message that is not the last message; 4 bytes if it is the last (or only segment) of a message.
 * These define the individual bits of these bytes:
 * <pre>
 *                                                     |<---- Present only if last segment bit set ----->|
 * <------- Byte 0 --------> <------- Byte 1 --------> <------- Byte 2 --------> <------- Byte 3 -------->
 * 7                       0 7                       0 7                       0 7                       0
 * +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+
 * |  |  |  |  |  |  |  |  | |  |  |  |  |  |  |  |  | |  |  |  |  |  |  |  |  | |  |  |  |  |  |  |  |  |
 * +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+ +--+--+--+--+--+--+--+--+
 *  ^   ^ <-  Segment No ->    ^  ^ <-M-> ^  ^  ^  ^    <-------------------- CRC ---------------------->
 *  |   |                      |  |   s   |  |  |  |
 *  |   Always 1            Always 0  g   |  |  | Resp Req
 *  |                                 I   |  | Acknowledgement
 * 1: Last Segment                    d   | Resend
 * 0: Part of Multi segment              CRC bit
 * 1100 0000 0000 1001
 * </pre>
 */
class FadHeader {


    /**
     * Defines the maximum size of a FAD header.
     */
    public static final int FAD_MAX_SIZE = 4;

    /**
     * byte-0, bit-7, If set, is the last segment. If clear, it is part of a
     * multi segment message.
     */
    public static final int FAD_LAST_SEGMENT_MASK = 0x80;

    /**
     * byte-0, bit-6, MUST always be set.
     */
    public static final int FAD_EXT_MASK = 0x40;

    /**
     * byte-0, bits 0-5 (6 bits) are the sequence number
     */
    public static final int FAD_SEGMENT_ID_MASK = 0x3F;

    /**
     * byte-1, bits 4 and 7 are the message ID
     */
    public static final int FAD_MESSAGE_ID_MASK = 0x30;
    public static final int MAX_MESSAGE_ID = 3;

    /**
     * byte-1, bit 3: If set, indicates that a CRC is included in the header and
     * should be checked.
     */
    public static final int FAD_CRC_MASK = 0x08;

    /**
     * byte-1, bit 2:
     */
    public static final int FAD_RESEND_MASK = 0x04;

    /**
     * byte-1, bit 1: If set, indicates the packet is a response packet. If
     * clear, is a segment packet.
     */
    public static final int FAD_ACK_MASK = 0x02;

    /**
     * byte-1, bit 0: If set, indicates the packet is a response packet. If
     * clear, is a segment packet.
     */
    public static final int FAD_ACKREQ_MASK = 0x01;

    public static final int FAD_MAX_MESSAGE_ID = 0x0f;
    public static final int FAD_MAX_SEGMENT_ID = 0x2f;
    public static final int FAD_MAX_CRC = 0x0000ffff;

    private static final int CRC_SEED_VALUE = 0xffff;

    private int crc;
    private int messageId;
    private int segmentId;
    private boolean lastSegment;
    private boolean hasCrc;
    private boolean resendRequest;
    private boolean acknowledgement;
    private boolean ackRequired;
    private CrcCalculator crcCalculator;

    public FadHeader() {
        setSegmentId(segmentId);
        this.crc = 0;
        this.segmentId = 0;
        this.messageId = 0;
        this.hasCrc = false;
        this.lastSegment = false;
        this.ackRequired = false;
        this.acknowledgement = false;
        this.crcCalculator = new Crc16Reflect();
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setAsResendRequestForMessage(int messageId) {
        setMessageId(messageId);
        setSegmentId(0);
        this.lastSegment = true;
        this.acknowledgement = false;
        this.ackRequired = false;
        this.resendRequest = true;
    }
    
    public void setAsAcknowledgmentHeader(int messageId, int segmentId) {
        setMessageId(messageId);
        setSegmentId(segmentId);
        this.lastSegment = true;
        this.acknowledgement = true;
        this.ackRequired = false;
        this.resendRequest = false;
    }

    public void setAsResendRequestForSegment(int messageId, int segmentId) {
        setMessageId(messageId);
        setSegmentId(segmentId);
        this.lastSegment = false;
        this.acknowledgement = false;
        this.ackRequired = false;
        this.resendRequest = true;
        this.hasCrc = false;
    }
    
    public void setAsSegmentHeaderWithoutCrc(int messageId, int segmentId) {
        setMessageId(messageId);
        setSegmentId(segmentId);
        this.lastSegment = false;
        this.acknowledgement = false;
        this.ackRequired = true;
        this.resendRequest = false;
        this.hasCrc = false;
    }

    public void setAsSegmentHeaderWithCrc(int messageId, int segmentId, int crc) {
        setMessageId(messageId);
        setSegmentId(segmentId);
        setCrc(crc);
        this.lastSegment = true;
        this.acknowledgement = false;
        this.ackRequired = true;
        this.resendRequest = false;
        this.hasCrc = true;
        this.crc = crc;
    }

    public boolean isLastSegment() {
        return lastSegment;
    }

    public boolean isAcknowledgement() {
        return acknowledgement;
    }

    public boolean isResendRequest() {
        return resendRequest;
    }

    public boolean isResendSegmentRequest() {
        return isResendRequest() && isLastSegment() == true;
    }
    
    public boolean isResendMessageRequest() {
        return isResendRequest() && isLastSegment();
    }
    
    public boolean isAcknowledgementRequired() {
        return ackRequired;
    }

    public int getCrc() {
        return crc;
    }

    public boolean hasCrc() {
        return hasCrc;
    }

    public void setMessageId(int messageId) {
        if (messageId > FAD_MAX_MESSAGE_ID)
            throw new IllegalArgumentException("Message ID is out of range. Must be less than " + FAD_MAX_MESSAGE_ID);
        this.messageId = messageId;
    }
    
    public void deserialize(ByteBuffer byteBuffer) throws IOException, CrcException {
        parseHeaderByte0FromByteBuffer(byteBuffer);
        parseHeaderByte1FromInputStream(byteBuffer);
        crc = hasCrc() ? (byteBuffer.getShort() & 0xffff) : 0;
        if (isControlHeader() && hasCrc) {
            byte[] bytes = new byte[]{valueForHeaderByte0(), valueForHeaderByte1()};
            if (crcCalculator.calculate(Fad.CRC_SEED, bytes) != crc)
                throw new CrcException("CRC error for header");
        }
    }

    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)valueForHeaderByte0());
        byteBuffer.put((byte)valueForHeaderByte1());
        if (isAcknowledgement()) {
            hasCrc = true;
            byteBuffer.putShort((short)crcForAckMessage());
        } else if (hasCrc())
            byteBuffer.putShort((short)crc);
    }

    public boolean isControlHeader() {
        return isAcknowledgement() || isResendRequest();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("msgId: ").append(messageId);
        stringBuilder.append(", segId: ").append(segmentId);
        stringBuilder.append(", crcInc: ").append(hasCrc() ? "t" : "f");
        stringBuilder.append(", resendReq: ").append(isResendRequest() ? "t" : "f");
        stringBuilder.append(", isLast: ").append(isLastSegment() ? "t" : "f");
        stringBuilder.append(", isAck: ").append(isAcknowledgement() ? "t" : "f");
        stringBuilder.append(", ackReq: ").append(isAcknowledgementRequired() ? "t" : "f");
        if (hasCrc())
            stringBuilder.append(", crc: ").append(crc).append("(0x").append(Integer.toHexString(crc)).append(")");
        return stringBuilder.toString();
    }
    
    private void setCrc(int crc) {
        if (crc > FAD_MAX_CRC)
            throw new IllegalArgumentException("CRC is out of range. Must be less than " + FAD_MAX_CRC);
        this.crc = crc;
    }
    
    private void setSegmentId(int segmentId) {
        if (segmentId > FAD_MAX_SEGMENT_ID)
            throw new IllegalArgumentException("Segment ID is out of range. Must be less than " + FAD_MAX_SEGMENT_ID);
        this.segmentId = segmentId;
    }

    private int crcForAckMessage() {
        byte[] bytes = new byte[]{valueForHeaderByte0(), valueForHeaderByte1()};
        return crcCalculator.calculate(CRC_SEED_VALUE, bytes);
    }

    private void parseHeaderByte0FromByteBuffer(ByteBuffer byteBuffer) throws IOException {
        int headerByte = byteBuffer.get();
        lastSegment = (headerByte & FAD_LAST_SEGMENT_MASK) != 0;
        segmentId = headerByte & FAD_SEGMENT_ID_MASK;
    }

    private void parseHeaderByte1FromInputStream(ByteBuffer byteBuffer) throws IOException {
        int headerByte = byteBuffer.get();
        hasCrc = (headerByte & FAD_CRC_MASK) != 0;
        resendRequest = (headerByte & FAD_RESEND_MASK) != 0;
        acknowledgement = (headerByte & FAD_ACK_MASK) != 0;
        ackRequired = (headerByte & FAD_ACKREQ_MASK) != 0;
        messageId = (headerByte & FAD_MESSAGE_ID_MASK) >> 4;
    }

    private byte valueForHeaderByte0() {
        byte byte0 = (byte)(isLastSegment() ? FAD_LAST_SEGMENT_MASK : 0);
        return (byte)(byte0 | FAD_EXT_MASK | segmentId);
    }

    private byte valueForHeaderByte1() {
        byte byte1 = (byte)((hasCrc() | isAcknowledgement()) ? FAD_CRC_MASK : 0);
        byte1 |= (byte)(isResendRequest() ? FAD_RESEND_MASK : 0);
        byte1 |= (byte)(isAcknowledgement() ? FAD_ACK_MASK : 0);
        byte1 |= (byte)(isAcknowledgementRequired() ? FAD_ACKREQ_MASK : 0);
        return (byte)(byte1 | (messageId << 4));
    }

    public static FadHeader headerFromByteBuffer(ByteBuffer byteBuffer) throws IOException, CrcException {
        FadHeader header = new FadHeader();
        header.deserialize(byteBuffer);
        return header;
    }

}
