package zedi.pacbridge.stp.apl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.crc.CheckSum;


public class AplEncoderTest extends BaseTestCase {

    @Mock
    private CheckSum checkSum;

    @Test
    public void shouldEncodeEscapeByteAndCheckSumThatIsEscapeByte() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class), anyInt(), anyInt()))
            .thenReturn((int)Apl.EOF);
        AplEncoder encoder = new AplEncoder(checkSum);
        encoder.encodeDataFromSrcBufferToDstBuffer(ByteBuffer.wrap(new byte[]{0x01}), byteBuffer);
        
        byteBuffer.flip();
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.SOF, byteBuffer.get());
        assertEquals(0x01, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.ESC_FOR_EOF, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.EOF, byteBuffer.get());
        assertFalse(byteBuffer.hasRemaining());
    }

    @Test
    public void shouldEncodeEscapeForEOF() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        when(checkSum.calculatedChecksumForByteArray(any(byte[].class), anyInt(), anyInt()))
            .thenReturn((int)0xFD);
        
        AplEncoder encoder = new AplEncoder(checkSum);
        encoder.encodeDataFromSrcBufferToDstBuffer(ByteBuffer.wrap(new byte[]{Apl.EOF}), byteBuffer);

        byteBuffer.flip();
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.SOF, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.ESC_FOR_EOF, byteBuffer.get());
        assertEquals((byte)0xFD, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.EOF, byteBuffer.get());
        assertFalse(byteBuffer.hasRemaining());
    }

    @Test
    public void shouldEncodeEscapeForSOF() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        when(checkSum.calculatedChecksumForByteArray(any(byte[].class), anyInt(), anyInt()))
            .thenReturn((int)0xFE);
    
        AplEncoder encoder = new AplEncoder(checkSum);
        encoder.encodeDataFromSrcBufferToDstBuffer(ByteBuffer.wrap(new byte[]{Apl.SOF}), byteBuffer);
        
        byteBuffer.flip();
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.SOF, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.ESC_FOR_SOF, byteBuffer.get());
        assertEquals((byte)0xFE, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.EOF, byteBuffer.get());
        assertFalse(byteBuffer.hasRemaining());
    }

    
    @Test
    public void shouldEncodeEscapeForESC() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        when(checkSum.calculatedChecksumForByteArray(any(byte[].class), anyInt(), anyInt()))
            .thenReturn((int)0xE5);
        
        AplEncoder encoder = new AplEncoder(checkSum);
        encoder.encodeDataFromSrcBufferToDstBuffer(ByteBuffer.wrap(new byte[]{Apl.ESC}), byteBuffer);

        byteBuffer.flip();
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.SOF, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals((byte)0xE5, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.EOF, byteBuffer.get());
        assertFalse(byteBuffer.hasRemaining());
    }

    @Test
    public void shouldEncodeAplBytesToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        when(checkSum.calculatedChecksumForByteArray(any(byte[].class), anyInt(), anyInt()))
            .thenReturn((int)0xF7);
        
        AplEncoder encoder = new AplEncoder(checkSum);
        encoder.encodeDataFromSrcBufferToDstBuffer(ByteBuffer.wrap(new byte[]{0x09}), byteBuffer);
        
        byteBuffer.flip();
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.SOF, byteBuffer.get());
        assertEquals(0x09, byteBuffer.get());
        assertEquals((byte)0xF7, byteBuffer.get());
        assertEquals(Apl.ESC, byteBuffer.get());
        assertEquals(Apl.EOF, byteBuffer.get());
        assertFalse(byteBuffer.hasRemaining());
    }
}
