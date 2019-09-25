package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class WriteCodeBlockCommandTest extends BaseTestCase {
    private static final Integer IDENTIFIER = 12;
    private static final Integer ADDRESS = 123;
    private static final byte[] CODE_BLOCK = new byte[]{0x01, 0x02, 0x03};
    
    @Test
    public void testSerializeCommand() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        WriteCodeBlockCommand command = new WriteCodeBlockCommand(IDENTIFIER, ADDRESS, CODE_BLOCK);
        
        command.serialize(byteBuffer);
        byteBuffer.flip();

        assertEquals((byte)0x04, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(WriteCodeBlockCommand.FIXED_SIZE+CODE_BLOCK.length, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(IDENTIFIER.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(0, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(ADDRESS.intValue(), Unsigned.getUnsignedInt(byteBuffer));
        assertEquals(CODE_BLOCK.length, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(1, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(2, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(3, Unsigned.getUnsignedByte(byteBuffer));
    }
}
