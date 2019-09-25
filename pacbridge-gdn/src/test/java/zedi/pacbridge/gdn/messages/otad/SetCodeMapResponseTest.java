package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.gdn.otad.CodeMap;
import zedi.pacbridge.test.BaseTestCase;

public class SetCodeMapResponseTest extends BaseTestCase {
    private static final Integer ID = 2;
    private CodeMap FLASHMAP = new CodeMap();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        FLASHMAP.addMapByte(1, 0);
        FLASHMAP.addMapByte(2, 1);
        FLASHMAP.addMapByte(3, 2);
    }

    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte)0x02);
        byteBuffer.putShort((short)(SetCodeMapResponse.FIXED_SIZE + FLASHMAP.mapBytes().length));
        byteBuffer.putShort(ID.shortValue());
        byteBuffer.putShort((short)0);
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)2);
        byteBuffer.put((byte)3);
        byteBuffer.flip();
        
        SetCodeMapResponse response = SetCodeMapResponse.setCodeMapResponsedFromByteBuffer(byteBuffer);
        assertEquals(ID, response.getIdentifier());
        assertTrue(Arrays.equals(FLASHMAP.getMapBytes(), response.getCodeMap().getMapBytes()));
    }
}
