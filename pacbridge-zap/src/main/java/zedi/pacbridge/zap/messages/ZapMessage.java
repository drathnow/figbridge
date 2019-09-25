package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import org.json.JSONObject;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.ZapSerializable;

public abstract class ZapMessage implements Message, ZapSerializable, Serializable {

    private ZapMessageType messageType;
    private Integer sequenceNumber = 0;
    
    protected ZapMessage() {
    }
    
    public ZapMessage(ZapMessageType messageType) {
        this(messageType, 0);
    }
    
    public ZapMessage(ZapMessageType messageType, Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        this.messageType = messageType;
    }
    
    @Override
    public MessageType messageType() {
        return messageType;
    }
    
    @Override
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    @Override
    public Integer sequenceNumber() {
        return sequenceNumber;
    }
    
    protected JSONObject baseJSONObject() {
        JSONObject json = new JSONObject();
        json.put("SeqNo", sequenceNumber);
        return json;
    }
}
