package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class LoadImageCommand extends OtadCommand implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 5; // This size includes the header!
    
    private Integer identifier;
    
    private LoadImageCommand(OtadMessageHeader messageHeader, Integer identifier) {
        super(messageHeader);
        this.identifier = identifier;
    }
    
    public LoadImageCommand(Integer identifier) {
        super(OtadMessageType.LoadImage);
        this.identifier = identifier;
    }

    public Integer getIdentifier() {
        return identifier;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        messageHeader.serialize(byteBuffer);
        byteBuffer.putShort(SIZE.shortValue());
        byteBuffer.putShort(identifier.shortValue());
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    public static LoadImageCommand loadImageCommandFromByteBuffer(ByteBuffer byteBuffer) {
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        byteBuffer.getShort(); // Skip the size
        Integer identifier = (int)byteBuffer.getShort();
        return new LoadImageCommand(header, identifier);
    }

    @Override
    public Long getEventId() {
        return null;
    }

}