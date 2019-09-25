package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;

public class LoadImageResponseTest extends BaseTestCase {
    private static final Integer IDENTIFIER = 12;

    @Test
    public void testSerializeOutput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        LoadImageResponse loadImageResponse = new LoadImageResponse(IDENTIFIER, ErrorCode.IncompleteImage);
        loadImageResponse.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(LoadImageResponse.SIZE.shortValue(), byteBuffer.remaining());
        assertEquals(0xB5, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(LoadImageResponse.SIZE.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(IDENTIFIER.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
    }

    @Test
    public void shouldDeserializeWithNoError() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put((byte)0x05);
        byteBuffer.putShort(LoadImageResponse.SIZE.shortValue());
        byteBuffer.putShort(IDENTIFIER.shortValue());
        byteBuffer.flip();
        LoadImageResponse command = LoadImageResponse.loadImageResponseFromByteBuffer(byteBuffer);
        
        assertEquals(IDENTIFIER, command.getIdentifier()); 
    }

    @Test
    public void shouldDeserializeWithError() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put((byte)0x25);
        byteBuffer.putShort(LoadImageResponse.SIZE.shortValue());
        byteBuffer.putShort(IDENTIFIER.shortValue());
        byteBuffer.flip();
        LoadImageResponse command = LoadImageResponse.loadImageResponseFromByteBuffer(byteBuffer);
        
        assertEquals(ErrorCode.InvalidCommandLength, command.getErrorCode());
        assertEquals(IDENTIFIER, command.getIdentifier());
    }
}
