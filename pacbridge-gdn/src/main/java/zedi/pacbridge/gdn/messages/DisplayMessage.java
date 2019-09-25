package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class DisplayMessage extends CommandMessage implements GdnMessage, Serializable {
    static final long serialVersionUID = 1001;

    public DisplayMessage(String message, Integer commandNumber) {
        super(GdnMessageType.PacConsoleDisplay, message.getBytes(), commandNumber);
    }

    public String getMessage() {
        return new String(asByteArray());
    }

    public static final DisplayMessage displayMessageFromByteBuffer(ByteBuffer byteBuffer) {
        Integer commandNumber = (int)byteBuffer.get();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new DisplayMessage(new String(bytes), commandNumber);
    }
}