package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapLongTest extends BaseTestCase {

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(45);
        byteBuffer.flip();
        
        ZapLong along = ZapLong.longFromByteBuffer(byteBuffer);
        assertEquals(45, along.getValue().intValue());
    }
}