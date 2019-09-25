package zedi.pacbridge.gdn.messages;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import zedi.pacbridge.test.BaseTestCase;

public class GdnPacketTest extends BaseTestCase {

    @Test
    public void shouldSerializePacket() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        SwtHeader header = mock(SwtHeader.class);
        GdnMessage message = mock(GdnMessage.class);
        
        InOrder inOrder = Mockito.inOrder(header, message);
        
        GdnPacket gdnPacket = new GdnPacket(header, message);
        gdnPacket.serialize(byteBuffer);
        
        inOrder.verify(header).serialize(eq(byteBuffer));
        inOrder.verify(message).serialize(eq(byteBuffer));
    }
}