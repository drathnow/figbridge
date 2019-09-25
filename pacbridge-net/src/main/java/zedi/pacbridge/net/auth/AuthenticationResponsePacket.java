package zedi.pacbridge.net.auth;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.collections.BufferUnderflowException;

public class AuthenticationResponsePacket {
    public static final Integer AUTH_SIZE = 6;
    public static final Integer NOAUTH_SIZE = 1;
    
    private boolean authenticated;
    private boolean expectingOutgoingRequests;
    private Integer systemId;

    public AuthenticationResponsePacket() {
        this(false, false, 0);
    }
    
    public AuthenticationResponsePacket(boolean authenticated, boolean expectingOutgoingRequests, Integer systemId) {
        this.authenticated = authenticated;
        this.expectingOutgoingRequests = expectingOutgoingRequests;
        this.systemId = systemId;
    }
 
    public void serialize(ByteBuffer byteBuffer) {
        if (authenticated) {
            byteBuffer.put(AUTH_SIZE.byteValue());
            byteBuffer.put((byte)1);
            byteBuffer.put((byte)(expectingOutgoingRequests ? 1 : 0));
            byteBuffer.putInt(systemId);
        } else {
            byteBuffer.put(NOAUTH_SIZE.byteValue());
            byteBuffer.put((byte)0);
        }
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public boolean isExpectingOutgoingRequests() {
        return expectingOutgoingRequests;
    }

    public Integer getSystemId() {
        return systemId;
    }
    
    public static AuthenticationResponsePacket packetFromByteBuffer(ByteBuffer byteBuffer) {
        boolean expectingOutgoingRequests = false;
        Integer systemId = 0;
        int size = byteBuffer.get();
        if (size < NOAUTH_SIZE)
            throw new BufferUnderflowException("Authentication response does not contain enough bytes");
        boolean authenticated = byteBuffer.get() == 1;
        if (size == NOAUTH_SIZE && authenticated == false)
            return new AuthenticationResponsePacket();
        if (authenticated && size < AUTH_SIZE)
            throw new BufferUnderflowException("Authentication response does not contain enough bytes");
        if (authenticated) {
            expectingOutgoingRequests = byteBuffer.get() == 1;
            systemId = byteBuffer.getInt();
        }
        return new AuthenticationResponsePacket(authenticated, expectingOutgoingRequests, systemId);
    }
    
    public static AuthenticationResponsePacket packetFromStream(DataInputStream inputStream) throws IOException {
        int size = inputStream.readByte();
        if (size < NOAUTH_SIZE)
            throw new BufferUnderflowException("Authentication response does not contain enough bytes");
        boolean authenticated = inputStream.readByte() == 1;
        if (size == NOAUTH_SIZE && authenticated == false)
            return new AuthenticationResponsePacket();
        if (authenticated && size < AUTH_SIZE)
            throw new BufferUnderflowException("Authentication response does not contain enough bytes");
        boolean expectingOutgoingRequests = inputStream.readByte() == 1;
        Integer systemId = inputStream.readInt();
        return new AuthenticationResponsePacket(authenticated, expectingOutgoingRequests, systemId);
    }
}
