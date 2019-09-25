package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.test.BaseTestCase;

public class ChallengeResponseMessageV2Test extends BaseTestCase {

    private static final byte[] CLIENT_SALT = new byte[]{0x01, 0x02, 0x03};
    private static final byte[] CLIENT_HASH = new byte[]{0x04, 0x05, 0x06, 0x07};
    private static final String USERNAME = "spooge";
    private static final Integer DEVICE_TIME = 123454;
    private static final String FIRMWARE_VERSION = "V1.2.3";
    
  
    @Test
    public void shouldDeserializePacket() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        byteBuffer.put((byte)31);
        byteBuffer.put(ChallengeResponseMessageV2.VERSION1.byteValue());
        byteBuffer.put((byte)EncryptionType.NONE.getNumber().intValue());
        byteBuffer.put((byte)CompressionType.NONE.getNumber().intValue());
        byteBuffer.putInt(DEVICE_TIME.intValue());
        byteBuffer.put((byte)CLIENT_SALT.length);
        byteBuffer.put(CLIENT_SALT);
        byteBuffer.put((byte)CLIENT_HASH.length);
        byteBuffer.put(CLIENT_HASH);
        byteBuffer.put((byte)USERNAME.length());
        byteBuffer.put(USERNAME.getBytes());
        byteBuffer.put((byte)FIRMWARE_VERSION.length());
        byteBuffer.put(FIRMWARE_VERSION.getBytes());
        byteBuffer.flip();

        ChallengeResponseMessageV2 response = ChallengeResponseMessageV2.clientChallengeResponseFromByteBuffer(byteBuffer);

        assertEquals(EncryptionType.NONE, response.getEncryptionType());
        assertEquals(CompressionType.NONE, response.getCompressionType());
        assertEquals(DEVICE_TIME, response.getDeviceTime());
        assertTrue(Arrays.equals(CLIENT_SALT, response.getClientSalt()));
        assertTrue(Arrays.equals(CLIENT_HASH, response.getClientHash()));
        assertEquals(USERNAME, response.getUsername());
        assertEquals(FIRMWARE_VERSION, response.getFirmwareVersion());
    }
}
