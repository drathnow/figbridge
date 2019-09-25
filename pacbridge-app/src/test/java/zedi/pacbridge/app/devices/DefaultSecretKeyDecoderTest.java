package zedi.pacbridge.app.devices;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DefaultSecretKeyDecoderTest extends BaseTestCase {

    private static final byte[] KEY_BYTES = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, (byte)0xFF};

    @Test
    public void shouldReturnBytesFromByteBufer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(KEY_BYTES);
        DefaultSecretKeyDecoder keyDecoder = new DefaultSecretKeyDecoder();
        byte[] results = keyDecoder.secretKeyFromByteBuffer(byteBuffer);
        assertTrue(Arrays.equals(KEY_BYTES, results));
    }
}
