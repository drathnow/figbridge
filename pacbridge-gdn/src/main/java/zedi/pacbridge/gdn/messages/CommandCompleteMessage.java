package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CommandCompleteMessage extends CommandMessage implements GdnMessage, Serializable {
    static final long serialVersionUID = 1001;

    public CommandCompleteMessage() {
        super(GdnMessageType.PacConsoleCloseCommand, new byte[]{0}, 0);
    }
    
    public static final CommandCompleteMessage commandCompleteMessageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new CommandCompleteMessage();
    }
}
