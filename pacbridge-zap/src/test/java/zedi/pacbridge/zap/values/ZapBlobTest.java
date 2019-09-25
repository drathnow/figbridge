package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapBlobTest extends BaseTestCase {

    private static final byte[] BLOB_VALUE = {0x00, 0x00, 0x00, 0x03, 0x01, 0x02, 0x03};
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(BLOB_VALUE);
        
        ZapBlob blob = ZapBlob.blobFromByteBuffer(byteBuffer);
        
        byte[] value = blob.getValue();
        assertEquals(3, value.length);
        assertEquals(1, value[0]);
        assertEquals(2, value[1]);
        assertEquals(3, value[2]);
    }
    
}
