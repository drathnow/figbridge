package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

public class AckMessageTest {

    private static final int SEGMENT_ID = 0;
    private static final int MESSAGE_ID = 2;

    @Test
    public void shouldTransmit() throws Exception {
        AckMessage ackMessage = new AckMessage(MESSAGE_ID, SEGMENT_ID);
        FadMessageTransmitter transmitter = mock(FadMessageTransmitter.class);
        
        assertEquals(4, ackMessage.size());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        ackMessage.transmitThroughMessageTransmitter(transmitter, byteBuffer);
        
        verify(transmitter).transmitByteBuffer(byteBuffer);
        assertEquals((byte)0xC0, byteBuffer.get());
        assertEquals((byte)0x2A, byteBuffer.get());
        assertEquals((byte)0xD0, byteBuffer.get());
        assertEquals((byte)0x6F, byteBuffer.get());
    }
}
