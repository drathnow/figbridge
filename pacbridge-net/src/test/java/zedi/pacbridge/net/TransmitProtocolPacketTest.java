package zedi.pacbridge.net;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringEncoder;

public class TransmitProtocolPacketTest extends BaseTestCase {

    private static final Integer MAX_PACKET = 10;
    private static final byte[] BYTES = new byte[]{0x01, 0x02, 0x03};
    
    private byte[] zeroedByteArray(int capacity) {
        byte[] buffer = new byte[capacity];
        for (int i = 0; i < capacity; i++)
            buffer[i] = 0;
        return buffer;
    }

    @Test
    public void shouldWriteBodyHeaderAndTrailerWithExpandedInternalBuffer() throws Exception {
        byte[] expected = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(MAX_PACKET, 4, 4);
        protocolPacket.expand();
        
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.put(BYTES);
        protocolPacket.setBodyLength(BYTES.length);
        protocolPacket.merge();
        
        protocolPacket.addHeader(2);
        ByteBuffer headerByteBuffer = protocolPacket.headerByteBuffer();
        headerByteBuffer.put((byte)0x0a);
        headerByteBuffer.put((byte)0x0b);
        
        protocolPacket.addTrailer(2);
        ByteBuffer trailerByteBuffer = protocolPacket.trailerByteBuffer();
        trailerByteBuffer.put((byte)0x0f);
        trailerByteBuffer.put((byte)0x0e);
        protocolPacket.merge();
        
        System.out.println("Foo: " + HexStringEncoder.bytesAsHexString(protocolPacket.buffer()));

        assertArrayEquals(expected, protocolPacket.buffer());
        
        assertEquals(7,  protocolPacket.bodyLength().intValue());
        byte[] otherBuffer = new byte[protocolPacket.bodyLength()];
        bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.get(otherBuffer);

        byte[] array = Arrays.copyOfRange(protocolPacket.buffer(), protocolPacket.body.offset(), protocolPacket.body.offset()+protocolPacket.body.length());
        byte[] expectedBytes = new byte[]{0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e};
        assertArrayEquals(expectedBytes, array);
    }

    
    @Test
    public void shouldWriteBodyHeaderAndTrailerWithInternalBuffer() throws Exception {
        byte[] expected = new byte[]{0x00, 0x00, 0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e, 0x00};
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(MAX_PACKET, 4, 4);
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.put(BYTES);
        protocolPacket.setBodyLength(BYTES.length);
        protocolPacket.merge();
        
        protocolPacket.addHeader(2);
        ByteBuffer headerByteBuffer = protocolPacket.headerByteBuffer();
        headerByteBuffer.put((byte)0x0a);
        headerByteBuffer.put((byte)0x0b);
        
        protocolPacket.addTrailer(2);
        ByteBuffer trailerByteBuffer = protocolPacket.trailerByteBuffer();
        trailerByteBuffer.put((byte)0x0f);
        trailerByteBuffer.put((byte)0x0e);
        protocolPacket.merge();
        
        assertArrayEquals(expected, protocolPacket.buffer());
        
        assertEquals(7,  protocolPacket.bodyLength().intValue());
        byte[] otherBuffer = new byte[protocolPacket.bodyLength()];
        bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.get(otherBuffer);

        byte[] expectedBytes = new byte[]{0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(otherBuffer);
        assertTrue(err, Arrays.equals(expectedBytes, otherBuffer));
    }
    
    @Test
    public void shouldWriteBodyHeaderAndTrailerWithWrappedBuffer() throws Exception {
        byte[] expected = new byte[]{0x00, 0x00, 0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e, 0x00};
        byte[] buffer = zeroedByteArray(MAX_PACKET);
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(buffer, 4, 4);
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.put(BYTES);
        protocolPacket.setBodyLength(BYTES.length);
        protocolPacket.merge();
        
        protocolPacket.addHeader(2);
        ByteBuffer headerByteBuffer = protocolPacket.headerByteBuffer();
        headerByteBuffer.put((byte)0x0a);
        headerByteBuffer.put((byte)0x0b);
        
        protocolPacket.addTrailer(2);
        ByteBuffer trailerByteBuffer = protocolPacket.trailerByteBuffer();
        trailerByteBuffer.put((byte)0x0f);
        trailerByteBuffer.put((byte)0x0e);
        protocolPacket.merge();
        
        assertTrue(Arrays.equals(expected, buffer));
        
        assertEquals(7,  protocolPacket.bodyLength().intValue());
        byte[] otherBuffer = new byte[protocolPacket.bodyLength()];
        bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.get(otherBuffer);

        byte[] expectedBytes = new byte[]{0x0a, 0x0b, 0x01, 0x02, 0x03, 0x0f, 0x0e};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(otherBuffer);
        assertTrue(err, Arrays.equals(expectedBytes, otherBuffer));
    }


    @Test
    public void shouldWriteToTrailerAfterBodyWithWrappedBuffer() throws Exception {
        byte[] buffer = zeroedByteArray(MAX_PACKET);
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(buffer, 4, 4);
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.put(BYTES);
        protocolPacket.setBodyLength(BYTES.length);
        protocolPacket.merge();
        
        protocolPacket.addTrailer(2);
        ByteBuffer trailerByteBuffer = protocolPacket.trailerByteBuffer();
        trailerByteBuffer.put((byte)0x0f);
        trailerByteBuffer.put((byte)0x0e);
        
        protocolPacket.merge();
        assertEquals(5,  protocolPacket.bodyLength().intValue());
        
        byte[] otherBuffer = new byte[protocolPacket.bodyLength()];
        bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.get(otherBuffer);

        byte[] expectedBytes = new byte[]{ 0x01, 0x02, 0x03, 0x0f, 0x0e};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(otherBuffer);
        assertTrue(err, Arrays.equals(expectedBytes, otherBuffer));
    }

    @Test
    public void shouldWriteToHeaderBeforeBodyWithWrappedBuffer() throws Exception {
        byte[] buffer = zeroedByteArray(MAX_PACKET);
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(buffer, 4, 4);
        ByteBuffer bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.put(BYTES);
        protocolPacket.setBodyLength(BYTES.length);
        assertEquals(3,  protocolPacket.bodyLength().intValue());
        
        protocolPacket.merge();
        assertEquals(3,  protocolPacket.bodyLength().intValue());

        protocolPacket.addHeader(2);
        ByteBuffer headerByteBuffer = protocolPacket.headerByteBuffer();
        headerByteBuffer.put((byte)0x0f);
        headerByteBuffer.put((byte)0x0e);
        
        protocolPacket.merge();
        assertEquals(5,  protocolPacket.bodyLength().intValue());
        
        byte[] otherBuffer = new byte[protocolPacket.bodyLength()];
        bodyByteBuffer = protocolPacket.bodyByteBuffer();
        bodyByteBuffer.get(otherBuffer);

        byte[] expectedBytes = new byte[]{ 0x0f, 0x0e, 0x01, 0x02, 0x03};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(otherBuffer);
        assertTrue(err, Arrays.equals(expectedBytes, otherBuffer));
    }
    
    @Test
    public void shouldWriteToBody() throws Exception {
        byte[] expected = new byte[]{0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00};
        byte[] buffer = zeroedByteArray(MAX_PACKET);
        
        TransmitProtocolPacket protocolPacket = new TransmitProtocolPacket(buffer, 4, 4);
        protocolPacket.bodyByteBuffer().put(BYTES);
        
        assertTrue(Arrays.equals(expected, buffer));
    }
}
