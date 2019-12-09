package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.net.Message;

public abstract class GdnMessageBase implements Message, Serializable {
    private static final long serialVersionUID = 1001;

    private GdnMessageType messageType;
    private Integer sequenceNumber;
    
    protected GdnMessageBase(GdnMessageType messageType) {
        this.messageType = messageType;
    }
    
    public GdnMessageType messageType() {
        return messageType;
    }
    
    @Override
    public Integer sequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    @Override
    public String toString() {
//        GdnMessageDecoder decoder = new GdnMessageDecoder();
//        return decoder.formattedMessage(this);
        return "Yeah, it's a message";
    }
}
