package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class MessageDeserializerTest extends BaseTestCase {

    private static final byte[] HELLO_WORLD_FAD_BYTES = HexStringDecoder.hexStringAsBytes("C0 09 ED DA 48 65 6C 6C 6F 20 57 6F 72 6C 64");
    private static final byte[] HELLO_WORLD_FAD_BYTES1 = HexStringDecoder.hexStringAsBytes("40 00 48 65 6C 6C 6F 20 57 6F"); 
    private static final int SEGMENT_ID = 0;
    private static final int MESSAGE_ID = 2;

    @Test
    public void shouldDeserializeResendMessageRequest() throws Exception {
        FadHeader fadHeader = new FadHeader();
        fadHeader.setAsResendRequestForMessage(MESSAGE_ID);
        
        assertTrue(fadHeader.isResendMessageRequest());
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        fadHeader.serialize(byteBuffer);
        byteBuffer.flip();
        
        MessageDeserializer messageDeserializer = new MessageDeserializer();
        FadMessage message = messageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        
        assertTrue(message instanceof ResendMessageRequest);
    }
    
    @Test
    public void shouldDeserializeResendSegmentRequest() throws Exception {
        FadHeader fadHeader = new FadHeader();
        fadHeader.setAsResendRequestForSegment(MESSAGE_ID, SEGMENT_ID);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        fadHeader.serialize(byteBuffer);
        byteBuffer.flip();
        
        MessageDeserializer messageDeserializer = new MessageDeserializer();
        FadMessage message = messageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        
        assertTrue(message instanceof ResendSegmentRequest);
    }
    
    @Test
    public void shouldDeserializeFirstSegment() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HELLO_WORLD_FAD_BYTES1);
        MessageDeserializer fadMessageDeserializer = new MessageDeserializer();
        FadMessage fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        Segment segmentMessage = (Segment)fadMessage;
        assertEquals("Hello Wo", new String(segmentMessage.getPayload()));
        
    }
    
    @Test
    public void shouldDeserializeFADMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HELLO_WORLD_FAD_BYTES);
        MessageDeserializer fadMessageDeserializer = new MessageDeserializer();
        FadMessage fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        Segment segmentMessage = (Segment)fadMessage;
        assertEquals("Hello World", new String(segmentMessage.getPayload()));
    }
}
