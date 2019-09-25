package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapShortTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xFF, (byte)0xFF});
        
        ZapShort aShort= ZapShort.shortFromByteBuffer(byteBuffer);
        assertEquals(-1, aShort.getValue().intValue());
    }
}