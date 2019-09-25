package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ConnectionFlagsTest extends BaseTestCase {

    @Test
    public void shouldSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        ConnectionFlags flags = new ConnectionFlags();

        flags.setAuthorized(true);
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals((byte)0x80, byteBuffer.get());
        byteBuffer.clear();
        
        flags.setOutBoundDataPending(true);
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals((byte)0xC0, byteBuffer.get());
        byteBuffer.clear();
        
        flags.setDelay(true);
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals((byte)0xE0, byteBuffer.get());
        byteBuffer.clear();
    }
    
    @Test
    public void shouldDeserialize() throws Exception {
        assertTrue(new ConnectionFlags(0x80).isAuthorized());
        assertTrue(new ConnectionFlags(0x40).isOutBoundDataPending());
        assertTrue(new ConnectionFlags(0x20).isDelay());
        
        ConnectionFlags flags = new ConnectionFlags(0xE0);
        assertTrue(flags.isAuthorized());
        assertTrue(flags.isOutBoundDataPending ());
        assertTrue(flags.isDelay());
    }
}
