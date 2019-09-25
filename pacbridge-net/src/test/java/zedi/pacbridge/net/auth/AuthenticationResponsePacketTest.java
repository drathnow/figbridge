package zedi.pacbridge.net.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

public class AuthenticationResponsePacketTest {
    private static final Integer SYSTEM_ID = 1234;
    
    @Test
    public void shouldDeserializePacket() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x02, 0x00});
        
        AuthenticationResponsePacket packet = AuthenticationResponsePacket.packetFromByteBuffer(byteBuffer);
        
        assertFalse(packet.isAuthenticated());
    }
    
    @Test
    public void shouldSerializePacketToByteBuffer() {
        AuthenticationResponsePacket packet = new AuthenticationResponsePacket(true, false, SYSTEM_ID);
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        
        packet.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(6, byteBuffer.get());
        assertEquals(1, byteBuffer.get());
        assertEquals(0, byteBuffer.get());
        assertEquals(SYSTEM_ID.intValue(), byteBuffer.getInt());
    }

    @Test
    public void shouldDeserializePacketFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        
        byteBuffer.put((byte)6);
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)1);
        byteBuffer.putInt(SYSTEM_ID);
        byteBuffer.flip();
        
        AuthenticationResponsePacket packet = AuthenticationResponsePacket.packetFromByteBuffer(byteBuffer);
        
        assertTrue(packet.isAuthenticated());
        assertTrue(packet.isExpectingOutgoingRequests());
        assertEquals(SYSTEM_ID, packet.getSystemId());
    }

    @Test
    public void shouldSerializePacketFromDataStream() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteStream);
        
        dataOutputStream.write((byte)6);
        dataOutputStream.write((byte)1);
        dataOutputStream.write((byte)1);
        dataOutputStream.writeInt(SYSTEM_ID);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        AuthenticationResponsePacket packet = AuthenticationResponsePacket.packetFromStream(dataInputStream);
        
        assertTrue(packet.isAuthenticated());
        assertTrue(packet.isExpectingOutgoingRequests());
        assertEquals(SYSTEM_ID, packet.getSystemId());
    }

}

