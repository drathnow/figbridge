package zedi.pacbridge.net.auth;

import java.nio.ByteBuffer;


public interface AuthenticationRequestPacket {

    public abstract String getPassword();

    public abstract Integer getNuid();

    public abstract void serialized(ByteBuffer byteBuffer);

}