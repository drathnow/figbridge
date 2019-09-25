package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ZapShort extends ZapValueBase implements ZapValue, Serializable {

    private Integer value;
    
    protected ZapShort() {
    }

    public ZapShort(Number value) {
        super(ZapDataType.Integer);
        this.value = value.intValue();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(value.shortValue());
    }

    @Override
    public Integer serializedSize() {
        return Short.SIZE/8;
    }

    public static ZapShort shortFromByteBuffer(ByteBuffer byteBuffer) {
        int value = byteBuffer.getShort();
        return new ZapShort(value);
    }

    public Integer getValue() {
        return value;
    }
}
