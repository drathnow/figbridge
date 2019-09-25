package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapUnsignedByteTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{(byte)0xFF});
        
        ZapUnsignedByte aByte = ZapUnsignedByte.unsignedByteFromByteBuffer(byteBuffer);
        assertEquals(255, aByte.getValue().intValue());
    }
}