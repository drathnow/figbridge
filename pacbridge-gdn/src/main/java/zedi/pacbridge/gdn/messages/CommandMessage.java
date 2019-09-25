package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;


public abstract class CommandMessage extends ByteMessage implements Serializable {
    static final long serialVersionUID = 1001;

    protected Integer commandNumber;

    protected CommandMessage(GdnMessageType messageType, byte[] bytes, Integer commandNumber) {
        super(messageType, bytes);
        this.commandNumber = commandNumber;
    }

    public Integer getCommandNumber() {
        return commandNumber;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(commandNumber.byteValue());
        byteBuffer.put(asByteArray());
    }
    
}