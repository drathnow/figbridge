package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ZapLong extends ZapValueBase implements ZapValue, Serializable {

    private Integer value;
    
    protected ZapLong() {
    }
    
    public ZapLong(Number value) {
        super(ZapDataType.Long);
        this.value = value.intValue();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(value.intValue());
    }

    @Override
    public Integer serializedSize() {
        return Integer.SIZE/8;
    }
    
    public static ZapLong longFromByteBuffer(ByteBuffer byteBuffer) {
        int value = byteBuffer.getInt();
        return new ZapLong(value);
    }

    public Integer getValue() {
        return value;
    }
}
