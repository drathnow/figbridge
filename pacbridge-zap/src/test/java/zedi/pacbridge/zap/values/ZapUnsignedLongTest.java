package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapUnsignedLongTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        
        ZapUnsignedLong value = ZapUnsignedLong.unsignedLongFromByteBuffer(byteBuffer);
        assertEquals(4294967295L, value.getValue().longValue());
    }
}