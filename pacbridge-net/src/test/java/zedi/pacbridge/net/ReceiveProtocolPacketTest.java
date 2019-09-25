package zedi.pacbridge.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringEncoder;

public class ReceiveProtocolPacketTest extends BaseTestCase {

    @Test
    public void shouldExtraceHeaderAndTrailer() throws Exception {
        byte[] bytes = new byte[]{ 0x0f, 0x0e, 0x01, 0x02, 0x03, 0x0a, 0x0b, 0x0c};
        ReceiveProtocolPacket protocolPacket = new ReceiveProtocolPacket(bytes);
        protocolPacket.extractHeader(2);
        protocolPacket.extractTrailer(3);
        
        ByteBuffer bb = protocolPacket.headerByteBuffer();
        assertEquals(0x0f, bb.get());
        assertEquals(0x0e, bb.get());
        
        bb = protocolPacket.trailerByteBuffer();
        assertEquals(0x0a, bb.get());
        assertEquals(0x0b, bb.get());
        assertEquals(0x0c, bb.get());
        
        byte[] result = new byte[3];
        protocolPacket.bodyByteBuffer().get(result);
        
        byte[] expectedBytes = new byte[]{0x01, 0x02, 0x03};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(result);
        assertTrue(err, Arrays.equals(expectedBytes, result));
        
        protocolPacket.trim();
        protocolPacket.bodyByteBuffer().get(result);
        assertTrue(err, Arrays.equals(expectedBytes, result));
    }
    
    @Test
    public void shouldExtraceHeaderFromRecieve() throws Exception {
        byte[] bytes = new byte[]{ 0x0f, 0x0e, 0x01, 0x02, 0x03};
        ReceiveProtocolPacket protocolPacket = new ReceiveProtocolPacket(bytes);
        protocolPacket.extractHeader(2);
        
        ByteBuffer bb = protocolPacket.headerByteBuffer();
        assertEquals(0x0f, bb.get());
        assertEquals(0x0e, bb.get());
        byte[] result = new byte[3];
        protocolPacket.bodyByteBuffer().get(result);
        
        byte[] expectedBytes = new byte[]{0x01, 0x02, 0x03};
        String err = "Expecting: " + HexStringEncoder.bytesAsHexString(expectedBytes) + ", but was " + HexStringEncoder.bytesAsHexString(result);
        assertTrue(err, Arrays.equals(expectedBytes, result));
        
        protocolPacket.trim();
        protocolPacket.bodyByteBuffer().get(result);
        assertTrue(err, Arrays.equals(expectedBytes, result));
    }
    
    @Test
    public void shouldExtractHeader() throws Exception {
        byte[] bytes = new byte[]{0x00, 0x02, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e};

        ReceiveProtocolPacket protocolPacket = new ReceiveProtocolPacket(bytes);
        protocolPacket.extractHeader(2);
        
        ByteBuffer bb = protocolPacket.headerByteBuffer();
        assertEquals(0x00, bb.get());
        assertEquals(0x02, bb.get());
        
        try {
            bb.get();
            fail();
        } catch (BufferUnderflowException e) {
        }
        
        protocolPacket.trim();
        protocolPacket.extractHeader(2);
        bb = protocolPacket.headerByteBuffer();
        assertEquals(0x0a, bb.get());
        assertEquals(0x0b, bb.get());
        
        try {
            bb.get();
            fail();
        } catch (BufferUnderflowException e) {
        }
    }
    
}
