package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapValue;
import zedi.pacbridge.zap.values.ZapValueDeserializer;

public class WriteValue implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer FIXED_SIZE = 5;
    
    private Long ioId;
    private ZapValue value;

    /**
     * We need this for serialization
     */
    @SuppressWarnings("unused")
    private WriteValue() {
    }
    
    public WriteValue(Long ioId, ZapValue value) {
        this.ioId = ioId;
        this.value = value;
    }

    public ZapValue getValue() {
        return value;
    }
    
    public Long getIoId() {
        return ioId;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(ioId.intValue());
        ZapDataType dataType = value.dataType();
        Integer typeNumber = dataType.getNumber();
        byteBuffer.put(typeNumber.byteValue());
        value.serialize(byteBuffer);
    }
    
    public Integer size() {
        return FIXED_SIZE + value.serializedSize(); 
    }
    
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("ioId", ioId);
        obj.put("type", value.dataType().getName());
        obj.put("value", value.toString());
        return obj.toString();
    }
    
    public static WriteValue writeValueFromByteBuffer(ByteBuffer byteBuffer) {
        Long ioId = Unsigned.getUnsignedInt(byteBuffer);
        Short typeNumber = Unsigned.getUnsignedByte(byteBuffer); 
        ZapDataType dataType = ZapDataType.dataTypeForTypeNumber(typeNumber.intValue());
        ZapValue value = ZapValueDeserializer.valueFromByteBuffer(dataType, byteBuffer);
        return new WriteValue(ioId, value);
    }
}
