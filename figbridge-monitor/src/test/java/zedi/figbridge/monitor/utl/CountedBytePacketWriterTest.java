package zedi.figbridge.monitor.utl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.Socket;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class CountedBytePacketWriterTest extends BaseTestCase {

    private static final byte[] BYTES = {0x01, 0x02, 0x03};
    
    @Test
    public void shouldWriteCountBytesBeforeRestOfPacket() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket = mock(Socket.class);
        
        given(socket.getOutputStream()).willReturn(outputStream);
        
        CountedBytePacketWriter writer = new CountedBytePacketWriter(socket);
        writer.sendBytes(BYTES, BYTES.length);
        
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals(3, inputStream.readUnsignedShort());
        assertEquals(0x01, inputStream.readByte());
        assertEquals(0x02, inputStream.readByte());
        assertEquals(0x03, inputStream.readByte());
    }
}
