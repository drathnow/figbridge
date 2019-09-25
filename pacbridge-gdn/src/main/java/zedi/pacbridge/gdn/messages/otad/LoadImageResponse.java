package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class LoadImageResponse extends OtadResponse implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 5; // This size includes the header!

    private Integer identifier;
    
    private LoadImageResponse(OtadMessageHeader messageHeader, Integer identifier) {
        super(messageHeader);
        this.identifier = identifier;
    }
    
    public LoadImageResponse(Integer identifier, ErrorCode errorCode) {
        super(new OtadMessageHeader(false, errorCode, OtadMessageType.LoadImage));
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

    public static LoadImageResponse loadImageResponseFromByteBuffer(ByteBuffer byteBuffer) {
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        int length = byteBuffer.getShort();
        Integer identifier = 0;
        if (length > 3)
            identifier = (int)byteBuffer.getShort();
        return new LoadImageResponse(header, identifier);
    }
}
