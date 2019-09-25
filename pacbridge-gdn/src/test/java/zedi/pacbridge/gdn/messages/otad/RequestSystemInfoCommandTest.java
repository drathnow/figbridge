package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class RequestSystemInfoCommandTest extends BaseTestCase {

    @Test
    public void testSerializeOutput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        RequestSystemInfoCommand command = new RequestSystemInfoCommand();
        command.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(RequestSystemInfoCommand.SIZE.intValue(), byteBuffer.remaining());
        assertEquals(0x01, byteBuffer.get());
        assertEquals(0x03, byteBuffer.getShort());
    }    
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        RequestSystemInfoCommand command = RequestSystemInfoCommand.requestSystemInfoCommandFromByteBuffer(byteBuffer);
        assertNotNull(command);
    }
}
