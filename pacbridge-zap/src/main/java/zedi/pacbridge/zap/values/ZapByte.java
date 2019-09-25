package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ZapByte extends ZapValueBase implements ZapValue, Serializable {

    private Integer value;
    
    protected ZapByte() {
    }
    
    public ZapByte(Number value) {
        super(ZapDataType.Byte);
        this.value = value.intValue();
    }
    
    public Integer getValue() {
        return value;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(value.byteValue());
    }

    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public Integer serializedSize() {
        return Byte.SIZE/8;
    }
    
    public static ZapByte byteFromByteBuffer(ByteBuffer byteBuffer) {
        int value = byteBuffer.get();
        return new ZapByte(value);
    }
}
