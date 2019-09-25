package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class AckMessage extends ZapMessage {
    public static final Integer FIXED_SIZE = 5;
    private static Integer ADDTIONAL_DETAILS_MASK = 0x01;
    private static Integer PROTOCOL_ERROR_MASK = 0x02;
    
    private MessageType ackedMessageType;
    private AckDetails additionalDetails;

    public class Flags {
        int theFlags;
        
        private Flags(boolean additionalDetails,
              boolean protocolError) {
            theFlags |= additionalDetails ? ADDTIONAL_DETAILS_MASK : 0;
            theFlags |= protocolError ? PROTOCOL_ERROR_MASK : 0;
        }
        
        private Flags(int theFlags) {
            this.theFlags = theFlags;
        }
        
        public boolean isAdditionalDetailsSet() {
            return (theFlags & ADDTIONAL_DETAILS_MASK) != 0;
        }

        public boolean isProtocolErrorSet() {
            return (theFlags & PROTOCOL_ERROR_MASK) != 0;
        }
        
        public JSONObject asJSONObject() {
            JSONObject json = baseJSONObject();
            json.put("Details", isAdditionalDetailsSet());
            json.put("ProtocolError", isProtocolErrorSet());
            return json;
        }
        
        @Override
        public String toString() {
            return asJSONObject().toString();
        }
    }
    
    public AckMessage(Integer sequenceNumber, MessageType ackedMessageType) {
        this(sequenceNumber, ackedMessageType, null);
    }
    
    public AckMessage(Integer sequenceNumber, MessageType ackedMessageType, AckDetails addtionalDetails) {
        super(ZapMessageType.Acknowledgement, sequenceNumber);
        this.ackedMessageType = ackedMessageType;
        this.additionalDetails = addtionalDetails;
    }
        
    public MessageType getAckedMessageType() {
        return ackedMessageType;
    }
    
    public boolean isProtocolError() {
        return additionalDetails != null && additionalDetails.type() == AckDetailsType.ProtocolError;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T additionalDetails() {
        return (T)additionalDetails;
    }
    
    public Flags flags() {
        boolean isProtocolError = additionalDetails == null ? false : (additionalDetails.type() == AckDetailsType.ProtocolError);
        return new Flags(additionalDetails != null, isProtocolError);
    }
    
    @Override
    public Integer size() {
        return FIXED_SIZE + (additionalDetails == null ? 0 : additionalDetails.size());
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        Flags flags = new Flags(additionalDetails != null, false);
        byteBuffer.putShort(size().shortValue());
        byteBuffer.put((byte)flags.theFlags);
        byteBuffer.putShort(ackedMessageType.getNumber().shortValue());
        byteBuffer.putShort(sequenceNumber().shortValue());
        if (additionalDetails != null)
            additionalDetails.serialize(byteBuffer);
    }

    public JSONObject asJSONObject() {
        JSONObject json = new JSONObject();
        json.put("MsgType", messageType().toString());
        json.put("AckMsgType", ackedMessageType);
        json.put("Flags", flags().asJSONObject());
        if (additionalDetails != null)
            json.put("Details", additionalDetails.asJSONObject());
        JSONObject obj = new JSONObject();
        obj.put(messageType().getName(), json);
        return obj;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
    
    public static AckMessage ackMessageForByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.getShort(); // skip over the size
        int flags = Unsigned.getUnsignedByte(byteBuffer);
        ZapMessageType messageType = ZapMessageType.messageTypeForNumber(Unsigned.getUnsignedShort(byteBuffer));
        Integer sequenceNumber = Unsigned.getUnsignedShort(byteBuffer);
        if ((flags & ADDTIONAL_DETAILS_MASK) != 0) {
            AckDetails details = AckDetails.ackDetailsFromByteBuffer(byteBuffer);
            return new AckMessage(sequenceNumber, messageType, details);
        } else
            return new AckMessage(sequenceNumber, messageType);
    }
}
