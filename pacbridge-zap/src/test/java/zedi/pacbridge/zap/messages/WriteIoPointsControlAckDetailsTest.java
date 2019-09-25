package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Map;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class WriteIoPointsControlAckDetailsTest extends BaseTestCase {

    private static final Long IOID1 = 42L;
    private static final Long IOID2 = 43L;
    private static final Integer STATUS1 = 0;
    private static final Integer STATUS2 = 1;

    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put((byte)2);
        byteBuffer.putInt(IOID1.intValue());
        byteBuffer.put(STATUS1.byteValue());
        byteBuffer.putInt(IOID2.intValue());
        byteBuffer.put(STATUS2.byteValue());
        byteBuffer.flip();
        
        WriteIoPointsControlAckDetails ackDetails = WriteIoPointsControlAckDetails.writeIoPointsMessageAckDetailsFromByteBuffer(byteBuffer);
        
        Map<Long, WriteValueAck> ackMap = ackDetails.ackMap();
        assertEquals(2, ackMap.size());
        WriteValueAck ack = ackMap.get(IOID1);
        assertNotNull(ack);
        assertEquals(IOID1, ack.iodId());
        assertTrue(ack.isSuccess());
        
        ack = ackMap.get(IOID2);
        assertNotNull(ack);
        assertEquals(IOID2, ack.iodId());
        assertFalse(ack.isSuccess());
    }
}
