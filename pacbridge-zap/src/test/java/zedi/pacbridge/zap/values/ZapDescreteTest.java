package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapDescreteTest extends BaseTestCase {

    private static final byte[] BLOB_VALUE = {0x03};
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(BLOB_VALUE);
        
        ZapDiscrete descrete = ZapDiscrete.descreteFromByteBuffer(byteBuffer);
        
        assertTrue(descrete.getValue().booleanValue());
    }
    
    @Test
    public void shouldReturnCorrecTrueString() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x01});
        ZapDiscrete descrete = ZapDiscrete.descreteFromByteBuffer(byteBuffer);
        assertEquals("1", descrete.toString());
    }
    
    @Test
    public void shouldReturnCorrecFalseString() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x00});
        ZapDiscrete descrete = ZapDiscrete.descreteFromByteBuffer(byteBuffer);
        assertEquals("0", descrete.toString());
    }
}
