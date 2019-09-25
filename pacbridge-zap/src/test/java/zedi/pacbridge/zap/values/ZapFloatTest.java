package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapFloatTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putFloat(100.1F);
        byteBuffer.flip();
        
        ZapFloat flo = ZapFloat.floatFromByteBuffer(byteBuffer);
        assertEquals(100.1F, flo.getValue().floatValue(), 0.01);
    }
}