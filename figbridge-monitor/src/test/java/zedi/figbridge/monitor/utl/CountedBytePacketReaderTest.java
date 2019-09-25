package zedi.figbridge.monitor.utl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class CountedBytePacketReaderTest extends BaseTestCase {
    private static final byte[] COUNTED_BYTES = {0x00, 0x03, 0x01, 0x02, 0x03};
    
    @Test
    public void shouldReadCountedBytesFromSocketStream() throws Exception {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(COUNTED_BYTES);
        Socket socket = mock(Socket.class);
        
        given(socket.getInputStream()).willReturn(arrayInputStream);
        
        byte[] buffer = new byte[10];
        
        CountedBytePacketReader reader = new CountedBytePacketReader(socket);
        assertEquals(3, reader.lengthOfNextPacket(buffer));
        assertEquals(0x01, buffer[0]);
        assertEquals(0x02, buffer[1]);
        assertEquals(0x03, buffer[2]);
    }
}
