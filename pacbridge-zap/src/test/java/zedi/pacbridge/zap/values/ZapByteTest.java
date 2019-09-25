package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapByteTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xFF});
        
        ZapByte aByte = ZapByte.byteFromByteBuffer(byteBuffer);
        assertEquals(-1, aByte.getValue().intValue());
    }
}