package zedi.pacbridge.stp.apl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.CheckSum;
import zedi.pacbridge.utl.crc.CheckSumException;


public class AplDecoderTest extends BaseTestCase {

    protected static final byte[] EOF_TEST = new byte[]{Apl.ESC, Apl.SOF, Apl.ESC, Apl.ESC_FOR_EOF, (byte)0xfd, Apl.ESC, Apl.EOF};
    protected static final byte[] SOF_TEST = new byte[]{Apl.ESC, Apl.SOF, Apl.ESC, Apl.ESC_FOR_SOF, (byte)0xfe, Apl.ESC, Apl.EOF};
    protected static final byte[] ESC_TEST = new byte[]{Apl.ESC, Apl.SOF, Apl.ESC, Apl.ESC, (byte)0xe5, Apl.ESC, Apl.EOF};
    protected static final byte[] MSG1_TEST = new byte[]{0x01, Apl.ESC, Apl.SOF, 'h', 'i', (byte)0x2f, Apl.ESC, Apl.EOF};
    protected static final byte[] MSG2_TEST = new byte[]{Apl.ESC, 0x01, 0x03, Apl.ESC, Apl.SOF, 'h', 'i', (byte)0x2f, Apl.ESC, Apl.EOF};
    protected static final byte[] MULTI_MSG_TEST = new byte[]{Apl.ESC, Apl.SOF, 'h', 'i', (byte)0x2f, Apl.ESC, Apl.EOF, Apl.ESC, Apl.SOF, 't', 'h', 'e', 'r', 'e', (byte)0xE8, Apl.ESC, Apl.EOF};
    protected static final byte[] NERA_BYTES = HexStringDecoder.hexStringAsBytes("|7E|21|45|00|01|72|00|21|00|00|40|11|62|E0|0A|8E|01|DE|0A|FF|FF|0F|5A|AA|5A|AA|01|5E|B2|6A|00|02|0A|8F|01|DE|C0|17|5D|84|08|34|08|34|1B|02|C0|39|A3|D4|0A|26|05|74|C6|22|49|2B|20|8E|1B|05|08|05|CC|08|A7|45|8F|1B|05|08|05|60|5C|A8|45|90|1B|05|08|07|20|17|17|3F|A2|1B|05|05|1B|05|68|10|A3|1B|05|05|00|14|12|A4|1B|05|05|00|64|0C|A5|1B|05|05|1B|04|88|13|A6|1B|05|05|00|B5|04|A7|1B|05|05|00|DD|00|A8|1B|05|05|00|47|01|A9|1B|05|05|00|40|01|AE|1B|05|05|00|00|00|AF|1B|05|05|00|00|00|B0|1B|05|05|00|00|00|B1|1B|05|05|00|00|00|B2|1B|05|05|00|00|00|B3|1B|05|05|00|00|00|B4|1B|05|05|00|01|00|B5|1B|05|05|00|00|00|B6|1B|05|05|00|0E|00|B7|1B|05|07|00|22|73|05|00|B8|1B|05|07|00|16|04|00|00|B9|1B|05|05|00|3B|81|BA|1B|05|05|00|5F|00|BB|1B|05|05|1B|04|ED|00|C0|1B|05|01|00|00|C1|1B|05|01|00|00|C2|1B|05|01|00|01|C3|1B|05|01|00|00|C4|1B|05|01|00|01|CC|1B|05|05|00|8E|4D|CD|1B|05|05|00|13|56|CE|1B|05|05|00|95|06|CF|1B|05|05|00|AA|06|D0|1B|05|05|00|1C|00|D1|1B|05|05|00|1B|05|00|D2|1B|05|05|00|4D|29|D3|1B|05|05|00|D8|48|D7|1B|05|05|00|1F|00|D8|1B|05|05|00|57|00|DA|1B|05|05|00|00|00|DB|1B|05|05|00|14|05|DE|1B|05|05|00|68|10|EC|1B|03|BA|D9|7E|");
    protected static final byte[] PAYLOAD = HexStringDecoder.hexStringAsBytes("|C0|39|A3|D4|0A|26|05|74|C6|22|49|2B|20|8E|03|08|05|CC|08|A7|45|8F|03|08|05|60|5C|A8|45|90|03|08|07|20|17|17|3F|A2|03|05|03|68|10|A3|03|05|00|14|12|A4|03|05|00|64|0C|A5|03|05|02|88|13|A6|03|05|00|B5|04|A7|03|05|00|DD|00|A8|03|05|00|47|01|A9|03|05|00|40|01|AE|03|05|00|00|00|AF|03|05|00|00|00|B0|03|05|00|00|00|B1|03|05|00|00|00|B2|03|05|00|00|00|B3|03|05|00|00|00|B4|03|05|00|01|00|B5|03|05|00|00|00|B6|03|05|00|0E|00|B7|03|07|00|22|73|05|00|B8|03|07|00|16|04|00|00|B9|03|05|00|3B|81|BA|03|05|00|5F|00|BB|03|05|02|ED|00|C0|03|01|00|00|C1|03|01|00|00|C2|03|01|00|01|C3|03|01|00|00|C4|03|01|00|01|CC|03|05|00|8E|4D|CD|03|05|00|13|56|CE|03|05|00|95|06|CF|03|05|00|AA|06|D0|03|05|00|1C|00|D1|03|05|00|03|00|D2|03|05|00|4D|29|D3|03|05|00|D8|48|D7|03|05|00|1F|00|D8|03|05|00|57|00|DA|03|05|00|00|00|DB|03|05|00|14|05|DE|03|05|00|68|10|");
    public static final byte SOF = 0x02;

    @Test
    public void shouldDecodeMessage() throws Exception {
        String byteString = "1B 02 C0 09 B1 B4 0C 26 00 7B 1B 05 4F 4F E9 D9 01 00 00 01 08 00 40 13 33 33 FF 1B 03";
        byte[] bytes = HexStringDecoder.hexStringAsBytes(byteString);
        AplDecoder decoder = new AplDecoder();
        decoder.decodeBytesFromByteBuffer(ByteBuffer.wrap(bytes));
        assertNotNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldDecodeMessageWithoutCheckSumException() throws Exception {
        String byteString = "|1B|02|C0|39|E7|E2|0A|26|FF|17|88|FB|4D|01|20|FF|00|04|00|01|00|1B|05|1B|03|";
        byte[] bytes = HexStringDecoder.hexStringAsBytes(byteString);
        AplDecoder decoder = new AplDecoder();
        decoder.decodeBytesFromByteBuffer(ByteBuffer.wrap(bytes));
        assertNotNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldDecodeSplitMessage() throws Exception {
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(5);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(MSG2_TEST.length - 5);
        byteBuffer1.put(MSG2_TEST, 0, 5);
        byteBuffer2.put(MSG2_TEST, 5, MSG2_TEST.length - 5);
        byteBuffer1.flip();
        byteBuffer2.flip();

        AplDecoder decoder = new AplDecoder();
        decoder.decodeBytesFromByteBuffer(byteBuffer1);
        assertNull(decoder.nextMessage());
        
        decoder.decodeBytesFromByteBuffer(byteBuffer2);
        byte[] nextPacket = decoder.nextMessage();
        assertEquals("hi", new String(nextPacket, 0, nextPacket.length));
        assertNull(decoder.nextMessage());
    }
    
    
    @Test
    public void shouldShouldDecodeEofFromByteBuffer() throws Exception {
        CheckSum checkSum = mock(AplCheckSum.class);
        
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class))).thenReturn(0xFD);
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(EOF_TEST);
        AplDecoder decoder = new AplDecoder(checkSum);
        decoder.decodeBytesFromByteBuffer(byteBuffer);
        byte[] nextPacket = decoder.nextMessage();
        
        assertEquals(1, nextPacket.length);
        assertEquals((byte)Apl.EOF, nextPacket[0]);
        assertNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldDecodeSofFromBuffer() throws Exception {
        CheckSum checkSum = mock(AplCheckSum.class);
        
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class))).thenReturn(0xFE);
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(SOF_TEST);
        AplDecoder decoder = new AplDecoder(checkSum);
        decoder.decodeBytesFromByteBuffer(byteBuffer);
        byte[] nextPacket = decoder.nextMessage();
        
        assertEquals(1, nextPacket.length);
        assertEquals((byte)Apl.SOF, nextPacket[0]);
        assertNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldDecodeEscFromBuffer() throws Exception {
        CheckSum checkSum = mock(AplCheckSum.class);

        when(checkSum.calculatedChecksumForByteArray(any(byte[].class))).thenReturn(0xE5);
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(ESC_TEST);
        AplDecoder decoder = new AplDecoder(checkSum);
        decoder.decodeBytesFromByteBuffer(byteBuffer);
        byte[] nextPacket = decoder.nextMessage();
        
        assertEquals(1, nextPacket.length);
        assertEquals((byte)Apl.ESC, nextPacket[0]);
        assertNull(decoder.nextMessage());
    }
        
    @Test
    public void shouldDecodeNERAPacket() throws Exception {
        AplDecoder decoder = new AplDecoder();
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(NERA_BYTES);
        decoder.decodeBytesFromByteBuffer(byteBuffer);
        byte[] nextPacket = decoder.nextMessage();
        
        assertEquals(276, nextPacket.length);
        assertTrue(Arrays.equals(PAYLOAD, nextPacket));
        assertNull(decoder.nextMessage());
    }
    
    @Test(expected = CheckSumException.class)
    public void shouldDetectBadChecksum() throws Exception {
        CheckSum checkSum = mock(AplCheckSum.class);
        AplDecoder decoder = new AplDecoder(checkSum);
        
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class))).thenReturn(0x00);
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(ESC_TEST);
        decoder.decodeBytesFromByteBuffer(byteBuffer);
    }

    @Test
    public void shouldDetectBadChecksumAndKeepGoing() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        CheckSum checkSum = mock(AplCheckSum.class);
        AplDecoder decoder = new AplDecoder(checkSum);
        
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class)))
            .thenReturn(0x00)
            .thenReturn(0xe5);
        
        byteBuffer.put(ESC_TEST);
        byteBuffer.put(ESC_TEST);
        byteBuffer.flip();
        try {
            decoder.decodeBytesFromByteBuffer(byteBuffer);
            fail();
        } catch (CheckSumException e) {
        }
        decoder.decodeBytesFromByteBuffer(byteBuffer);
        byte[] nextMessage = decoder.nextMessage();
        assertEquals(1, nextMessage.length);
        assertEquals(Apl.ESC, nextMessage[0]);
        assertNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldDecodeMultipleMessages() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(MULTI_MSG_TEST);
        AplDecoder decoder = new AplDecoder();
        decoder.decodeBytesFromByteBuffer(byteBuffer);

        byte[] nextPacket = decoder.nextMessage();
        assertEquals("hi", new String(nextPacket, 0, nextPacket.length));
        nextPacket = decoder.nextMessage();
        assertEquals("there", new String(nextPacket, 0, nextPacket.length));
        assertNull(decoder.nextMessage());
    }
    
    @Test
    public void shouldSkipBytesToFindFrame() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte)0xFF);
        byteBuffer.put((byte)0xFF);
        byteBuffer.put(MSG1_TEST);
        byteBuffer.flip();
        
        AplDecoder decoder = new AplDecoder();
        decoder.decodeBytesFromByteBuffer(byteBuffer);

        byte[] nextPacket = decoder.nextMessage();
        assertEquals("hi", new String(nextPacket, 0, nextPacket.length));
        assertNull(decoder.nextMessage());
    }
}
