package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class RequestTimeMessageTest extends BaseTestCase {


    @Test
    public void testSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        Date clientTimestamp = new Date();
        Date serverTimestamp = new Date();
        
        RequestTimeMessage message = new RequestTimeMessage(clientTimestamp, serverTimestamp);
        message.serialize(byteBuffer);
        byteBuffer.flip();

        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(clientTimestamp.getTime()/1000, Unsigned.getUnsignedInt(byteBuffer));
        assertEquals(serverTimestamp.getTime()/1000, Unsigned.getUnsignedInt(byteBuffer));
    }

    @Test
    public void testDeserialize() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        Date clientTimestamp = new Date();
        Date serverTimestamp = new Date();
        
        byteBuffer.put((byte)0);
        byteBuffer.putInt((int)(clientTimestamp.getTime()/1000));
        byteBuffer.putInt((int)(serverTimestamp.getTime()/1000));
        byteBuffer.flip();
        
        RequestTimeMessage message = RequestTimeMessage.RequestTimeMessageFromByteBuffer(byteBuffer);
        assertTrue(message.isRequest());
        assertEquals(clientTimestamp.getTime() / 1000L, message.getClientTimestamp().getTime() / 1000L);
        assertEquals(serverTimestamp.getTime() / 1000L, message.getServerTimestamp().getTime() / 1000L);
    }
}
