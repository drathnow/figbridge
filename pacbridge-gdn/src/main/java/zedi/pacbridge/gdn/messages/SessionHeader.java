package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;


public interface SessionHeader {
    public Integer getSessionId();
    public GdnMessageType messageType();
    public void serialize(ByteBuffer byteBuffer);
}
