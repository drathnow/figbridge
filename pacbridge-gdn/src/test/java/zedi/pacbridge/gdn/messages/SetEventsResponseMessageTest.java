package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Collections;

import org.junit.Test;

import zedi.pacbridge.gdn.PacEventStatus;


public class SetEventsResponseMessageTest {

    public static final int INDEX1 = 1;
    public static final int INDEX2 = 2;
    
    @Test
    public void testMessageNumber() throws Exception {
        SetEventsResponseMessage responseMessage = new SetEventsResponseMessage(Collections.<Integer, PacEventStatus> emptyMap());
        assertEquals(GdnMessageType.SetEventsResponse, responseMessage.messageType());
    }
    
    @Test
    public void testToString() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)0);
        byteBuffer.putShort((short)2);
        byteBuffer.putShort((short)INDEX1);
        byteBuffer.put((byte)PacEventStatus.TooManyEvents.getStatusNumber());
        byteBuffer.putShort((short)INDEX2);
        byteBuffer.put((byte)PacEventStatus.InvalidParameter.getStatusNumber());
        byteBuffer.flip();
        
        SetEventsResponseMessage responseMessage = SetEventsResponseMessage.setEventsResponseMessageFromByteBuffer(byteBuffer);

        assertEquals("{(1, Maximum number of events exceeded), (2, Invalid Parameter)}", responseMessage.toString());
    }
    
    @Test
    public void testDeserializeWithMix() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)0); // version
        byteBuffer.putShort((short)2); // Count
        byteBuffer.putShort((short)INDEX1);
        byteBuffer.put((byte)PacEventStatus.Success.getStatusNumber());
        byteBuffer.putShort((short)INDEX2);
        byteBuffer.put((byte)PacEventStatus.InvalidParameter.getStatusNumber());
        byteBuffer.flip();
        
        SetEventsResponseMessage responseMessage = SetEventsResponseMessage.setEventsResponseMessageFromByteBuffer(byteBuffer);

        assertTrue(responseMessage.containsFailures());
        assertTrue(responseMessage.successfulIndexes().contains(INDEX1));
        assertTrue(responseMessage.failedIndexes().contains(INDEX2));
    }
    
    @Test
    public void testDeserializeWithFailures() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)0); // version
        byteBuffer.putShort((short)2); // Count
        byteBuffer.putShort((short)INDEX1);
        byteBuffer.put((byte)PacEventStatus.TooManyEvents.getStatusNumber());
        byteBuffer.putShort((short)INDEX2);
        byteBuffer.put((byte)PacEventStatus.InvalidParameter.getStatusNumber());
        byteBuffer.flip();
        
        SetEventsResponseMessage responseMessage = SetEventsResponseMessage.setEventsResponseMessageFromByteBuffer(byteBuffer);

        assertTrue(responseMessage.containsFailures());
        assertEquals(0, responseMessage.successfulIndexes().size());
        assertEquals(2, responseMessage.failedIndexes().size());
    }

    @Test
    public void testDeserializeWithSuccesses() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)0); // version
        byteBuffer.putShort((short)2); // Count
        byteBuffer.putShort((short)INDEX1);
        byteBuffer.put((byte)PacEventStatus.Success.getStatusNumber());
        byteBuffer.putShort((short)INDEX2);
        byteBuffer.put((byte)PacEventStatus.Success.getStatusNumber());
        byteBuffer.flip();
        
        SetEventsResponseMessage responseMessage = SetEventsResponseMessage.setEventsResponseMessageFromByteBuffer(byteBuffer);

        assertFalse(responseMessage.containsFailures());
        assertEquals(2, responseMessage.successfulIndexes().size());
        assertEquals(0, responseMessage.failedIndexes().size());
    }
}
