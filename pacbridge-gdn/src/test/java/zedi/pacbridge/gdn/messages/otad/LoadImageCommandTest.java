package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class LoadImageCommandTest extends BaseTestCase {

    private static final Integer IDENTIFIER = 12;

    @Test
    public void testSerializeOutput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        LoadImageCommand loadImageCommand = new LoadImageCommand(IDENTIFIER);
        loadImageCommand.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(LoadImageCommand.SIZE.shortValue(), byteBuffer.remaining());
        assertEquals(0x05, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(LoadImageCommand.SIZE.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(IDENTIFIER.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
    }

    @Test
    public void testSerializeInput() throws IOException {
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put((byte)0x05);
        byteBuffer.putShort(LoadImageCommand.SIZE.shortValue());
        byteBuffer.putShort(IDENTIFIER.shortValue());
        byteBuffer.flip();
        LoadImageCommand command = LoadImageCommand.loadImageCommandFromByteBuffer(byteBuffer);
        
        assertEquals(IDENTIFIER, command.getIdentifier()); 
    }
}
