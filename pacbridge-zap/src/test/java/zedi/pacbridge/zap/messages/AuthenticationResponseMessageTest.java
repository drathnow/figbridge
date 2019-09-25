package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.ZapMessageType;

public class AuthenticationResponseMessageTest extends BaseTestCase {
    private static final Integer DEVICE_TIME = 123;
    private static final Integer SERVER_TIME = 456;
    private static final String SERVER_NAME = "Spooge";
    private static final byte[] SERVER_HASH = new byte[]{0x01, 0x02, 0x03};
    private static final byte[] SESSION_KEY = new byte[]{0x04, 0x05, 0x06, 0x07};
    
    
    @Test
    public void shouldSerializeNonAuthenticatedResponse() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        AuthenticationResponseMessage response = new AuthenticationResponseMessage();
        assertEquals(ZapMessageType.AuthenticationResponse, response.messageType());
        response.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(AuthenticationResponseMessage.FIXED_SIZE.byteValue(), byteBuffer.get());
        assertEquals((byte)0x00, byteBuffer.get());
    }
    
    @Test
    public void shouldSerializeNullStringsAsZero() throws Exception {
        ConnectionFlags connectionFlags = new ConnectionFlags();
        connectionFlags.setAuthorized(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        AuthenticationResponseMessage response = new AuthenticationResponseMessage(connectionFlags, DEVICE_TIME, SERVER_TIME, SERVER_NAME, null, null);
        
        response.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(19, byteBuffer.get());
        assertEquals((byte)0x80, byteBuffer.get());

        assertEquals(DEVICE_TIME.intValue(), byteBuffer.getInt());
        assertEquals(SERVER_TIME.intValue(), byteBuffer.getInt());
        
        assertEquals((byte)6, byteBuffer.get());
        assertEquals((byte)'S', byteBuffer.get());
        assertEquals((byte)'p', byteBuffer.get());
        assertEquals((byte)'o', byteBuffer.get());
        assertEquals((byte)'o', byteBuffer.get());
        assertEquals((byte)'g', byteBuffer.get());
        assertEquals((byte)'e', byteBuffer.get());
        
        assertEquals((byte)0, byteBuffer.get());
        assertEquals((byte)0, byteBuffer.get());
        
    }
    
    @Test
    public void shouldSerialize() throws Exception {
        ConnectionFlags connectionFlags = new ConnectionFlags();
        connectionFlags.setAuthorized(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        AuthenticationResponseMessage response = new AuthenticationResponseMessage(connectionFlags, DEVICE_TIME, SERVER_TIME, SERVER_NAME, SERVER_HASH, SESSION_KEY);
        
        response.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(26, byteBuffer.get());
        assertEquals((byte)0x80, byteBuffer.get());

        assertEquals(DEVICE_TIME.intValue(), byteBuffer.getInt());
        assertEquals(SERVER_TIME.intValue(), byteBuffer.getInt());

        
        assertEquals((byte)6, byteBuffer.get());
        assertEquals((byte)'S', byteBuffer.get());
        assertEquals((byte)'p', byteBuffer.get());
        assertEquals((byte)'o', byteBuffer.get());
        assertEquals((byte)'o', byteBuffer.get());
        assertEquals((byte)'g', byteBuffer.get());
        assertEquals((byte)'e', byteBuffer.get());
        
        assertEquals((byte)3, byteBuffer.get());
        assertEquals((byte)1, byteBuffer.get());
        assertEquals((byte)2, byteBuffer.get());
        assertEquals((byte)3, byteBuffer.get());
        
        assertEquals((byte)4, byteBuffer.get());
        assertEquals((byte)4, byteBuffer.get());
        assertEquals((byte)5, byteBuffer.get());
        assertEquals((byte)6, byteBuffer.get());
        assertEquals((byte)7, byteBuffer.get());
    }
}
