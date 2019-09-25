package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;


public class SwtHeaderFactory {

    public SwtHeader newSessionHeaderWithSessionIdAndMessageType(int sessionId, GdnMessageType messageType) {
        return new SwtHeader12(sessionId, messageType);
    }

    public SwtHeader headerFromByteBuffer(ByteBuffer byteBuffer) {
        return SwtHeader.headerFromByteBuffer(byteBuffer);
    }

    public SwtHeader newSessionlessHeaderWithMessageType(GdnMessageType gdnMessageType) {
        return new SwtHeader10(gdnMessageType);
    }
}
