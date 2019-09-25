package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.Crc16Reflect;

public class SegmentTest {

    private static final int SEGMENT_ID = 0;
    private static final int CRC = 0xEDDA;
    private static final int MESSAGE_ID = 0;
    private static final byte[] TEST_BYTES = "Hello World".getBytes();
    private static final byte[] HELLO_WORLD_FAD_BYTES = HexStringDecoder.hexStringAsBytes("C0 09 DA ED 48 65 6C 6C 6F 20 57 6F 72 6C 64");
    private static final byte[] TEST_BYTES2 = new byte[] {(byte)0xC0, (byte)0x09, (byte)0xED, (byte)0xDA, (byte)0x48, (byte)0x65, (byte)0x6C, (byte)0x6C, (byte)0x6F, (byte)0x20, (byte)0x57, (byte)0x6F, (byte)0x72, (byte)0x6C, (byte)0x64};

    @Test
    public void shouldReturnCorrectSize() throws Exception {
        Segment segment = new Segment(1024, ByteBuffer.wrap(TEST_BYTES), MESSAGE_ID, SEGMENT_ID);
        assertEquals(FadHeader.FAD_MAX_SIZE+TEST_BYTES.length, segment.size());
    }
    
    @Test
    public void shouldDecodeFragment() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_BYTES2).order(ByteOrder.LITTLE_ENDIAN);
        FadHeader fadHeader = FadHeader.headerFromByteBuffer(byteBuffer);
        Crc16Reflect crc16Reflect = new Crc16Reflect();
        int crc = crc16Reflect.calculate(Fad.CRC_SEED, new byte[] {(byte)0x48, (byte)0x65, (byte)0x6C, (byte)0x6C, (byte)0x6F, (byte)0x20, (byte)0x57, (byte)0x6F, (byte)0x72, (byte)0x6C, (byte)0x64});
        assertEquals("Expected: 0x" 
                    + (Integer.toHexString(crc & 0xffff))
                    + ", was: 0x" 
                    + (Integer.toHexString(fadHeader.getCrc() & 0xffff)), crc, fadHeader.getCrc());
    }
    
    @Test
    public void shouldConsumeByteUpToMaxSize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_BYTES);
        Segment messageSegment = new Segment(6, byteBuffer, MESSAGE_ID, 1);
        assertEquals("Hello ", new String(messageSegment.getPayload()));
        assertEquals(1, messageSegment.getSegmentId());
        
        messageSegment = new Segment(6, byteBuffer, MESSAGE_ID, 2);
        assertEquals("World", new String(messageSegment.getPayload()));
        assertEquals(2, messageSegment.getSegmentId());
    }

    @Test
    public void shouldConsumeAllBytesWhenMaxSizeIsZero() throws Exception {
        Segment messageSegment = new Segment(0, ByteBuffer.wrap(TEST_BYTES), MESSAGE_ID, SEGMENT_ID);
        assertEquals(new String(TEST_BYTES), new String(messageSegment.getPayload()));
        assertEquals(SEGMENT_ID, messageSegment.getSegmentId());
    }
    
    @Test
    public void shouldConsumeUpToMaxBytesInMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_BYTES);
        new Segment(6, byteBuffer, MESSAGE_ID, SEGMENT_ID);
        assertTrue(byteBuffer.hasRemaining());
        new Segment(6, byteBuffer, MESSAGE_ID, SEGMENT_ID);
        assertFalse(byteBuffer.hasRemaining());
    }
    
    @Test
    public void shouldSendFormattedMessage() throws Exception {
        FadMessageTransmitter transmitter = mock(FadMessageTransmitter.class);
        Segment segment = new Segment(1024, ByteBuffer.wrap(TEST_BYTES), MESSAGE_ID, SEGMENT_ID);
        segment.setCrc(CRC);
        ByteBuffer byteBuffer = ByteBuffer.allocate(segment.size()).order(ByteOrder.LITTLE_ENDIAN);
        
        segment.transmitThroughMessageTransmitter(transmitter, byteBuffer);
        
        byte[] payload = new byte[byteBuffer.limit()];
        byteBuffer.get(payload);
        assertTrue(Arrays.equals(HELLO_WORLD_FAD_BYTES, payload));
    }
}
