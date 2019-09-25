package zedi.pacbridge.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ProtocolPacketBaseTest extends BaseTestCase {
    private static final Integer MAX_PACKET = 10;

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowAWrappedBufferToBeExpanded() throws Exception {
        TestableProtocolPacketBase protocolPacket = new TestableProtocolPacketBase(new byte[MAX_PACKET], 4, 4);
        protocolPacket.expand();
    }
    
    @Test
    public void shouldExpandBufferAndResetAndAdjustRanges() throws Exception {
        TestableProtocolPacketBase protocolPacket = new TestableProtocolPacketBase(MAX_PACKET, 4, 4);
        protocolPacket.expand();

        assertEquals(MAX_PACKET*2, protocolPacket.buffer().length);
        assertEquals(8, protocolPacket.body.offset());
        assertEquals(8, protocolPacket.body.length());
        assertEquals(8, protocolPacket.header.offset());
        assertEquals(0, protocolPacket.header.length());
        assertEquals(protocolPacket.body.offset()+protocolPacket.body.length(), protocolPacket.trailer.offset());
        assertEquals(0, protocolPacket.trailer.length());
        
        protocolPacket.reset();
        assertEquals(MAX_PACKET.intValue(), protocolPacket.buffer().length);
        assertEquals(4, protocolPacket.body.offset());
        assertEquals(4, protocolPacket.body.length());
        assertEquals(4, protocolPacket.header.offset());
        assertEquals(0, protocolPacket.header.length());
        assertEquals(protocolPacket.body.offset()+protocolPacket.body.length(), protocolPacket.trailer.offset());
        assertEquals(0, protocolPacket.trailer.length());
    }
   
    @Test
    public void shouldExpandBufferAndAdjustRanges() throws Exception {
        TestableProtocolPacketBase protocolPacket = new TestableProtocolPacketBase(MAX_PACKET, 4, 4);
        protocolPacket.expand();

        assertEquals(MAX_PACKET*2, protocolPacket.buffer().length);
        assertEquals(8, protocolPacket.body.offset());
        assertEquals(8, protocolPacket.body.length());
        assertEquals(8, protocolPacket.header.offset());
        assertEquals(0, protocolPacket.header.length());
        assertEquals(protocolPacket.body.offset()+protocolPacket.body.length(), protocolPacket.trailer.offset());
        assertEquals(0, protocolPacket.trailer.length());
    }
        
    private class TestableProtocolPacketBase extends ProtocolPacketBase {

        public TestableProtocolPacketBase(byte[] buffer, int bodyOffset, int bodyLength) {
            super(buffer, bodyOffset, bodyLength);
        }
        
        public TestableProtocolPacketBase(int maxPacketSize, int bodyOffset, int bodyLength) {
            super(maxPacketSize, bodyOffset, bodyLength);
        }
    }
}
