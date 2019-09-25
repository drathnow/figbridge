package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.gdn.otad.CodeMap;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class SetCodeMapCommandTest extends BaseTestCase {
    private static final byte INTERNALEEPROMMAP = 0;
    private static final byte EXTERNALEEPROMMAP = 0;
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
    public void shouldSerialize() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        SetCodeMapCommand setCodeMapCommand = new SetCodeMapCommand(ID, FLASHMAP);
        setCodeMapCommand.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(0x02, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(SetCodeMapCommand.FIXED_SIZE + FLASHMAP.mapBytes().length, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(ID.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(INTERNALEEPROMMAP, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(EXTERNALEEPROMMAP, Unsigned.getUnsignedByte(byteBuffer));
        byte[] mapBytes = FLASHMAP.mapBytes();
        assertEquals(mapBytes[0], Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(mapBytes[1], Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(mapBytes[2], Unsigned.getUnsignedByte(byteBuffer));
    }
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte)0x02);
        byteBuffer.putShort((short)(SetCodeMapCommand.FIXED_SIZE + FLASHMAP.mapBytes().length));
        byteBuffer.putShort(ID.shortValue());
        byteBuffer.putShort((short)0);
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)2);
        byteBuffer.put((byte)3);
        byteBuffer.flip();
        
        SetCodeMapCommand command = SetCodeMapCommand.setCodeMapCommandFromByteBuffer(byteBuffer);
        assertEquals(ID, command.getIdentifier());
        assertTrue(Arrays.equals(FLASHMAP.getMapBytes(), command.getCodeMap().getMapBytes()));
    }
}
