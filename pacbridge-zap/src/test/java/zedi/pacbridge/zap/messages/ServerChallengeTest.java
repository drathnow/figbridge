package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ServerChallengeTest extends BaseTestCase {
    public static final byte[] saltValue = new byte[]{0x01, 0x02, 0x03, 0x04};
    
    @Test
    public void shouldSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        ServerChallenge challenge = new ServerChallenge(saltValue);
        challenge.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(0x04, byteBuffer.get());
        assertEquals(0x01, byteBuffer.get());
        assertEquals(0x02, byteBuffer.get());
        assertEquals(0x03, byteBuffer.get());
        assertEquals(0x04, byteBuffer.get());
    }
}
