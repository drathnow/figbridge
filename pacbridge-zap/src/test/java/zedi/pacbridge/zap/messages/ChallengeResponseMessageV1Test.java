package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.test.BaseTestCase;

public class ChallengeResponseMessageV1Test extends BaseTestCase {

    private static final byte[] CLIENT_SALT = new byte[]{0x01, 0x02, 0x03};
    private static final byte[] CLIENT_HASH = new byte[]{0x04, 0x05, 0x06, 0x07};
    private static final String USERNAME = "spooge";
    private static final Integer DEVICE_TIME = 123454;
    
  
    @Test
    public void shouldDeserializePacket() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)12);
        byteBuffer.put((byte)EncryptionType.NONE.getNumber().intValue());
        byteBuffer.put((byte)CompressionType.NONE.getNumber().intValue());
        byteBuffer.putInt(DEVICE_TIME.intValue());
        byteBuffer.put((byte)CLIENT_SALT.length);
        byteBuffer.put(CLIENT_SALT);
        byteBuffer.put((byte)CLIENT_HASH.length);
        byteBuffer.put(CLIENT_HASH);
        byteBuffer.put((byte)USERNAME.length());
        byteBuffer.put(USERNAME.getBytes());
        byteBuffer.flip();

        ChallengeResponseMessageV1 response = ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);

        assertEquals(EncryptionType.NONE, response.getEncryptionType());
        assertEquals(CompressionType.NONE, response.getCompressionType());
        assertEquals(DEVICE_TIME, response.getDeviceTime());
        assertTrue(Arrays.equals(CLIENT_SALT, response.getClientSalt()));
        assertTrue(Arrays.equals(CLIENT_HASH, response.getClientHash()));
        assertEquals(USERNAME, response.getUsername());
    }
}
