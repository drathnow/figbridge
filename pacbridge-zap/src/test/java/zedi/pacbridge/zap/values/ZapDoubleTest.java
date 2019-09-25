package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapDoubleTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putDouble(100.1D);
        byteBuffer.flip();
        
        ZapDouble dub = ZapDouble.doubleFromByteBuffer(byteBuffer);
        assertEquals(100.1D, dub.getValue().doubleValue(), 0.01);
    }
}