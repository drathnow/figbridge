package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class WriteValueAckTest extends BaseTestCase {
    
    private static final Long IOID = 43L;
    private static final Integer STATUS = 0;
    
    @Test
    public void shouldShouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putInt(IOID.intValue());
        byteBuffer.put(STATUS.byteValue());
        byteBuffer.flip();
        
        WriteValueAck ack = WriteValueAck.writeValueAckFromByteBuffer(byteBuffer);
        assertEquals(IOID, ack.iodId());
        assertTrue(ack.isSuccess());
    }
}
