package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CommandLineMessage extends CommandMessage implements GdnMessage, Serializable {

    public CommandLineMessage(String commandLine, Integer commandNumber) {
        super(GdnMessageType.PacConsoleCommand, commandLine.getBytes(), commandNumber);
    }
    
    public String getCommandLine() {
        return new String(asByteArray());
    }
    
    public static final CommandLineMessage commandLineMessageFromByteBuffer(ByteBuffer byteBuffer) {
        Integer commandNumber = (int)byteBuffer.get();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new CommandLineMessage(new String(bytes), commandNumber);
    }

}
