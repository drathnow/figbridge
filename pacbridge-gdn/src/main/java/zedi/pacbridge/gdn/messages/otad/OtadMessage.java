package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.gdn.messages.GdnMessageBase;
import zedi.pacbridge.gdn.messages.GdnMessageType;

public abstract class OtadMessage extends GdnMessageBase implements GdnMessage, Serializable {
    private static final long serialVersionUID = 1001L;
    
    protected OtadMessageHeader messageHeader;
    
    protected OtadMessage(OtadMessageHeader messageHeader) {
        super(GdnMessageType.Otad);
        this.messageHeader = messageHeader;
    }

    public boolean isCommand() {
        return messageHeader.isCommand();
    }
    
    public boolean isResponse() {
        return messageHeader.isResponse();
    }
    
    public OtadMessageType getOtadMessageType() {
        return messageHeader.getMessageType();
    }
    
    public static final OtadMessage otadMessageFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.mark();
        OtadMessageHeader header = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        byteBuffer.reset();
        if (header.getMessageType() == OtadMessageType.RequestSystemInfo) {
            if (header.isCommand())
                return RequestSystemInfoCommand.requestSystemInfoCommandFromByteBuffer(byteBuffer);
            else
                return RequestSystemInfoResponse.requestSystemInfoResponseFromByteBuffer(byteBuffer);
        }
        
        if (header.getMessageType() == OtadMessageType.WriteCodeBlock) {
            if (header.isCommand())
                return WriteCodeBlockCommand.writeCodeBlockCommandFromByteBuffer(byteBuffer);
            else
                return WriteCodeBlockResponse.writeCodeBlockResponseFromByteBuffer(byteBuffer);
        }
        
        if (header.getMessageType() == OtadMessageType.SetCodeMap) {
            if (header.isCommand())
                return SetCodeMapCommand.setCodeMapCommandFromByteBuffer(byteBuffer);
            else
                return SetCodeMapResponse.setCodeMapResponsedFromByteBuffer(byteBuffer);
        }
        if (header.getMessageType() == OtadMessageType.LoadImage) {
            if (header.isCommand())
                return LoadImageCommand.loadImageCommandFromByteBuffer(byteBuffer);
            else
                return LoadImageResponse.loadImageResponseFromByteBuffer(byteBuffer);
        }
        throw new IllegalArgumentException("Invalid OTAD message type: " 
                + header.getMessageType());
        
    }
}
