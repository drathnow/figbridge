package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.values.ZapDataType;

public class IoPointTemplate {
    Long index;
    ZapDataType dataType;
    
    public IoPointTemplate(Long index, ZapDataType dataType) {
        this.index = index;
        this.dataType = dataType;
    }

    public Long index() {
        return index;
    }
    
    public ZapDataType dataType() {
        return dataType;
    }
    
    @Override
    public String toString() {
        return index + '/' + dataType.getName();
    }
    
    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("Index", index.toString());
        obj.put("DataType", dataType.getName());
        return obj;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(index.intValue());
        byteBuffer.put(dataType.getNumber().byteValue());
    }
    
    public static IoPointTemplate templateFromByteBuffer(ByteBuffer byteBuffer) {
        long index = Unsigned.getUnsignedInt(byteBuffer);
        ZapDataType dataType = ZapDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        return new IoPointTemplate(index, dataType);
    }
}