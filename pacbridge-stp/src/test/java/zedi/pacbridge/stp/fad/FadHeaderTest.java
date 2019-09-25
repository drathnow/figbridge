package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;


public class FadHeaderTest {

    
    public static final byte[] HEADER_WITH_NO_CRC_BYTES = new byte[]{0x42, (byte)0xA0};
    public static final byte[] HEADER_WITH_CRC_BYTES = new byte[]{(byte)0x42, (byte)0xA9, (byte)0xBA, 0x45};

    public static final int SEGMENT_ID = 42;
    public static final int MESSAGE_ID = 2;
    public static final int CRC = 42;
    
    @Test
    public void shouldSetAsAcknowledgementHeader() throws Exception {
        FadHeader header = new FadHeader();
        header.setAsAcknowledgmentHeader(MESSAGE_ID, SEGMENT_ID);
        
        assertTrue(header.isLastSegment());
        assertFalse(header.isResendRequest());
        assertTrue(header.isAcknowledgement());
        assertFalse(header.isAcknowledgementRequired());
        
        ByteBuffer buffer = ByteBuffer.allocate(4);
        header.serialize(buffer);
        buffer.flip();
        assertEquals((byte)0xEA, buffer.get());
        assertEquals((byte)0x2A, (byte)buffer.get());
    }
    
    public void shouldSetAsSegmentHeaderWithoutCrc() {
        FadHeader header = new FadHeader();
        
        header.setAsSegmentHeaderWithoutCrc(MESSAGE_ID, SEGMENT_ID);

        assertFalse(header.isLastSegment());
        assertFalse(header.isResendRequest());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isAcknowledgementRequired());
        assertFalse(header.hasCrc());
    }
    
    public void shouldSetAsSegmentHeaderWithCrc() {
        FadHeader header = new FadHeader();
        
        header.setAsSegmentHeaderWithCrc(MESSAGE_ID, SEGMENT_ID, CRC);

        assertTrue(header.isLastSegment());
        assertFalse(header.isResendRequest());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isAcknowledgementRequired());
        assertTrue(header.hasCrc());
        assertEquals(CRC, header.getCrc());
    }
    
    @Test
    public void shouldSerializeAckHeader() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        FadHeader header = new FadHeader();
        header.setAsAcknowledgmentHeader(MESSAGE_ID, SEGMENT_ID);

        header.serialize(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        
        assertEquals(4, byteBuffer.limit());
        
        assertEquals(FadHeader.FAD_LAST_SEGMENT_MASK, (bytes[0] & FadHeader.FAD_LAST_SEGMENT_MASK));
        assertEquals(SEGMENT_ID, bytes[0] & FadHeader.FAD_SEGMENT_ID_MASK);
        assertEquals(FadHeader.FAD_EXT_MASK, (bytes[0] & FadHeader.FAD_EXT_MASK));
        
        assertEquals(MESSAGE_ID, (bytes[1] & FadHeader.FAD_MESSAGE_ID_MASK) >> 4);
        assertEquals(0,(bytes[1] & FadHeader.FAD_RESEND_MASK));
        assertEquals(FadHeader.FAD_ACK_MASK, (bytes[1] & FadHeader.FAD_ACK_MASK));
        assertEquals(0, (bytes[1] & FadHeader.FAD_ACKREQ_MASK));
        assertEquals(FadHeader.FAD_CRC_MASK, (bytes[1] & FadHeader.FAD_CRC_MASK));
        
        assertEquals(0x0F, (int)(bytes[2] & 0x00FF));
        assertEquals(0xCF, (int)(bytes[3] & 0x00FF));
        
    }
    
    @Test
    public void shouldDeserializeLastSegmentHeader() throws Exception {
        byte[] bytes = new byte[]{(byte)0xC0, 0x09, (byte)0xED, (byte)0xDA};
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        
        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertTrue(header.isLastSegment());
        assertEquals(0, header.getSegmentId());
        assertFalse(header.isAcknowledgement());
        assertTrue(header.hasCrc());
        assertFalse(header.isResendRequest());
        assertTrue(header.isAcknowledgementRequired());
    }
    
    @Test
    public void shouldDeserializeAckHeader() throws Exception {
        byte[] bytes = new byte[]{(byte)0xC0, 0x0A, (byte)0xB7, (byte)0xD1};
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        
        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertTrue(header.isLastSegment());
        assertEquals(0, header.getSegmentId());
        assertTrue(header.isAcknowledgement());
        assertTrue(header.hasCrc());
        assertFalse(header.isResendRequest());
        assertFalse(header.isAcknowledgementRequired());
    }

    @Test
    public void shouldShouldDeserializeHeaderWithNoCRCFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_WITH_NO_CRC_BYTES.length);
        byteBuffer.put(HEADER_WITH_NO_CRC_BYTES);
        byteBuffer.flip();
        
        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertFalse(header.isLastSegment());
        assertEquals(2, header.getSegmentId());
        assertFalse(header.hasCrc());
        assertEquals(2, header.getMessageId());
        assertFalse(header.isAcknowledgementRequired());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isResendRequest());
    }
    
    @Test
    public void shouldShouldDeserializeHeaderWithNoCRC() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HEADER_WITH_NO_CRC_BYTES);
        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertFalse(header.isLastSegment());
        assertEquals(2, header.getSegmentId());
        assertFalse(header.hasCrc());
        assertEquals(2, header.getMessageId());
        assertFalse(header.isAcknowledgementRequired());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isResendRequest());
    }

    @Test
    public void shouldShouldDeserializeHeaderWithCRCFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_WITH_CRC_BYTES.length);
        byteBuffer.put(HEADER_WITH_CRC_BYTES);
        byteBuffer.flip();

        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertFalse(header.isLastSegment());
        assertEquals(2, header.getSegmentId());
        assertTrue(header.hasCrc());
        assertEquals(2, header.getMessageId());
        assertTrue(header.isAcknowledgementRequired());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isResendRequest());
        
        assertEquals(47685, header.getCrc());
    }
    
    @Test
    public void shouldShouldDeserializeHeaderWithCRC() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HEADER_WITH_CRC_BYTES);
        FadHeader header = FadHeader.headerFromByteBuffer(byteBuffer);
        
        assertFalse(header.isLastSegment());
        assertEquals(2, header.getSegmentId());
        assertTrue(header.hasCrc());
        assertEquals(2, header.getMessageId());
        assertTrue(header.isAcknowledgementRequired());
        assertFalse(header.isAcknowledgement());
        assertFalse(header.isResendRequest());
        
        assertEquals(47685, header.getCrc());
    }

    @Test
    public void shouldSerializeHeaderWithCrc() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        FadHeader header = new FadHeader();
        header.setAsSegmentHeaderWithCrc(MESSAGE_ID, SEGMENT_ID, 1234);

        header.serialize(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        
        assertEquals(4, byteBuffer.limit());
        
        assertEquals(FadHeader.FAD_LAST_SEGMENT_MASK, (bytes[0] & FadHeader.FAD_LAST_SEGMENT_MASK));
        assertEquals(SEGMENT_ID, bytes[0] & FadHeader.FAD_SEGMENT_ID_MASK);
        assertEquals(FadHeader.FAD_EXT_MASK, (bytes[0] & FadHeader.FAD_EXT_MASK));
        
        assertEquals(MESSAGE_ID, (bytes[1] & FadHeader.FAD_MESSAGE_ID_MASK) >> 4);
        assertEquals(0,(bytes[1] & FadHeader.FAD_RESEND_MASK));
        assertEquals(0, (bytes[1] & FadHeader.FAD_ACK_MASK));
        assertEquals(FadHeader.FAD_ACKREQ_MASK, (bytes[1] & FadHeader.FAD_ACKREQ_MASK));
        assertEquals(FadHeader.FAD_CRC_MASK, (bytes[1] & FadHeader.FAD_CRC_MASK));
        
        assertEquals(0x04, bytes[2]);
        assertEquals(0xd2, (int)(bytes[3] & 0x00FF));
    }
    
    @Test
    public void shouldSerializeHeaderForMultiSegmentHeader() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        FadHeader header = new FadHeader();
        header.setAsSegmentHeaderWithCrc(MESSAGE_ID, SEGMENT_ID, 1234);

        header.serialize(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        
        assertEquals(4, byteBuffer.limit());
        
        assertEquals(FadHeader.FAD_LAST_SEGMENT_MASK, bytes[0] & FadHeader.FAD_LAST_SEGMENT_MASK);
        assertEquals(SEGMENT_ID, bytes[0] & FadHeader.FAD_SEGMENT_ID_MASK);
        assertTrue((bytes[0] & FadHeader.FAD_EXT_MASK) != 0);
        
        assertEquals(MESSAGE_ID, (bytes[1] & FadHeader.FAD_MESSAGE_ID_MASK) >> 4);
        assertTrue((bytes[1] & FadHeader.FAD_RESEND_MASK) == 0);
        assertEquals(0, bytes[1] & FadHeader.FAD_ACK_MASK);
        assertTrue((bytes[1] & FadHeader.FAD_ACKREQ_MASK) == 1);
        assertEquals(FadHeader.FAD_CRC_MASK, (bytes[1] & FadHeader.FAD_CRC_MASK));
    }
}
