package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.Crc16Reflect;
import zedi.pacbridge.utl.crc.CrcCalculator;
import zedi.pacbridge.utl.crc.CrcException;

public class PendingMessageTest {

    private static final byte[] TEST_MSG = "Hello World".getBytes();
    private static final int MESSAGE_ID = 1;
    private static final String FAD_BYTES = "C0 19 39 58 0A 26 01 E7 5D FB 4D 26 20 9F 03 00 05 A0 03 00 05 A1 03 00 07 A2 03 00 07 A3 03 00 07 A4 03 00 07 A5 03 00 07 A6 03 00 07 AB 03 00 07 AC 03 00 07 AD 03 00 07 AE 03 00 07 AF 03 00 07 B0 03 00 07 B1 03 00 07 B2 03 00 07 B3 03 00 07 B4 03 00 07 B5 03 00 07 B6 03 00 07 B7 03 00 07 B8 03 00 07 BD 03 00 07 BE 03 00 07 BF 03 00 07 C0 03 00 07 C1 03 00 07 C8 03 00 07 C9 03 00 07 CA 03 00 07 CB 03 00 07 CC 03 00 07 CD 03 00 07 CE 03 00 07 CF 03 00 07 D3 03 00 07 D5 03 00 07 D6 03 00 07";
    
    @Test
    public void shouldDecodeMultiSegmentMessageFromOldStp() throws Exception {
        byte[][] oldStpSegments = new byte[][]
                {
                    HexStringDecoder.hexStringAsBytes("40 00 0C 26 00 7B 03 4F 51"),
                    HexStringDecoder.hexStringAsBytes("41 00 19 5B 01 00 00 01 08 00"), 
                    HexStringDecoder.hexStringAsBytes("C2 09 78 D1 40 13 33 33"),
                };

        MessageDeserializer deserializer = new MessageDeserializer();
        PendingMessage pendingMessage = null;
        for (byte[] bytes : oldStpSegments) {
            Segment fadSegment = (Segment)deserializer.fadMessageFromByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
            if (pendingMessage == null)
                pendingMessage = new PendingMessage(fadSegment.getMessageId());
            pendingMessage.addSegment(fadSegment);
        }
        
        pendingMessage.getMessage();

    }
    
    @Test
    public void shouldDecodeMessageFromOldStp() throws Exception {
        String oldStpByteString = "C0 09 B1 B4 0C 26 00 7B 03 4F 4F E9 D9 01 00 00 01 08 00 40 13 33 33";
        byte[] bytes = HexStringDecoder.hexStringAsBytes(oldStpByteString);
        MessageDeserializer deserializer = new MessageDeserializer();
        Segment fadSegment = (Segment)deserializer.fadMessageFromByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
        PendingMessage pendingMessage = new PendingMessage(fadSegment.getMessageId());
        pendingMessage.addSegment(fadSegment);
        pendingMessage.getMessage();
    }
    
    @Test
    public void shouldDecodeMessageWithoutExeption() throws Exception {
        MessageDeserializer deserializer = new MessageDeserializer();
        Segment fadSegment = (Segment)deserializer.fadMessageFromByteBuffer(ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(FAD_BYTES)).order(ByteOrder.LITTLE_ENDIAN));
        PendingMessage pendingMessage = new PendingMessage(fadSegment.getMessageId());
        pendingMessage.addSegment(fadSegment);
        pendingMessage.getMessage();
    }
    
    @Test(expected = CrcException.class)
    public void shouldDetectedBadCheckSum() throws Exception {
        CrcCalculator crc = mock(CrcCalculator.class);
        Segment segment = mock(Segment.class);
        
        when(crc.calculate(Fad.CRC_SEED, TEST_MSG)).thenReturn(1);
        when(segment.isLastSegment()).thenReturn(true);
        when(segment.getCrc()).thenReturn(2);
        when(segment.getPayload()).thenReturn(TEST_MSG);
        
        PendingMessage pendingMessage = new PendingMessage(MESSAGE_ID, crc);
        pendingMessage.addSegment(segment);
        
        pendingMessage.getMessage();
    }
    
    @Test
    public void shouldIndicateWhenMessageIsCompleteForMultiSegmentMessageWhenRecievedOutOfOrder() throws Exception {
        Segment fadSegment1 = mock(Segment.class);
        Segment fadSegment2 = mock(Segment.class);
        Segment fadSegment3 = mock(Segment.class);
        
        when(fadSegment1.getSegmentId()).thenReturn(0);
        when(fadSegment1.isLastSegment()).thenReturn(false);
        when(fadSegment1.getPayload()).thenReturn("Hell".getBytes());
        
        when(fadSegment2.getSegmentId()).thenReturn(1);
        when(fadSegment2.isLastSegment()).thenReturn(false);
        when(fadSegment2.getPayload()).thenReturn("o Wor".getBytes());

        when(fadSegment3.getSegmentId()).thenReturn(2);
        when(fadSegment3.isLastSegment()).thenReturn(true);
        when(fadSegment3.getPayload()).thenReturn("ld".getBytes());
        when(fadSegment3.getCrc()).thenReturn(new Crc16Reflect().calculate(Fad.CRC_SEED, TEST_MSG));

        PendingMessage pendingMessage = new PendingMessage(MESSAGE_ID);
        
        pendingMessage.addSegment(fadSegment3);
        assertFalse(pendingMessage.isComplete());
        
        pendingMessage.addSegment(fadSegment1);
        assertFalse(pendingMessage.isComplete());

        pendingMessage.addSegment(fadSegment2);
        assertTrue(pendingMessage.isComplete());

        assertTrue(pendingMessage.isComplete());
        assertTrue(Arrays.equals(TEST_MSG, pendingMessage.getMessage()));
    }
    
    @Test
    public void shouldIndicateWhenMessageIsCompleteForMultiSegmentMessage() throws Exception {
        Segment fadSegment1 = mock(Segment.class);
        Segment fadSegment2 = mock(Segment.class);
        Segment fadSegment3 = mock(Segment.class);
        
        when(fadSegment1.getSegmentId()).thenReturn(0);
        when(fadSegment1.isLastSegment()).thenReturn(false);
        when(fadSegment1.getPayload()).thenReturn("Hell".getBytes());
        
        when(fadSegment2.getSegmentId()).thenReturn(1);
        when(fadSegment2.isLastSegment()).thenReturn(false);
        when(fadSegment2.getPayload()).thenReturn("o Wor".getBytes());

        when(fadSegment3.getSegmentId()).thenReturn(2);
        when(fadSegment3.isLastSegment()).thenReturn(true);
        when(fadSegment3.getPayload()).thenReturn("ld".getBytes());
        when(fadSegment3.getCrc()).thenReturn(new Crc16Reflect().calculate(Fad.CRC_SEED, TEST_MSG));

        PendingMessage pendingMessage = new PendingMessage(MESSAGE_ID);
        
        pendingMessage.addSegment(fadSegment1);
        assertFalse(pendingMessage.isComplete());

        pendingMessage.addSegment(fadSegment2);
        assertFalse(pendingMessage.isComplete());

        pendingMessage.addSegment(fadSegment3);
        assertTrue(pendingMessage.isComplete());
        
        assertTrue(pendingMessage.isComplete());
        assertTrue(Arrays.equals(TEST_MSG, pendingMessage.getMessage()));
    }
    
    
    @Test
    public void shouldIndicateWhenMessageIsCompleteForSingleSegmentMessage() throws Exception {
        Segment fadSegment = mock(Segment.class);
        
        when(fadSegment.getSegmentId()).thenReturn(0);
        when(fadSegment.isLastSegment()).thenReturn(true);
        when(fadSegment.getPayload()).thenReturn(TEST_MSG);
        when(fadSegment.getCrc()).thenReturn(new Crc16Reflect().calculate(Fad.CRC_SEED, TEST_MSG));
        
        PendingMessage pendingMessage = new PendingMessage(MESSAGE_ID);
        
        pendingMessage.addSegment(fadSegment);
        
        assertTrue(pendingMessage.isComplete());
        assertTrue(Arrays.equals(TEST_MSG, pendingMessage.getMessage()));
    }
}
