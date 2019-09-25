package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class ZapUnsignedShort extends ZapValueBase implements ZapValue, Serializable {

    private Integer value;
    
    protected ZapUnsignedShort() {
    }

    public ZapUnsignedShort(Number value) {
        super(ZapDataType.UnsignedInteger);
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
    
    public static ZapUnsignedShort unsignedShortFromByteBuffer(ByteBuffer byteBuffer) {
        int value = Unsigned.getUnsignedShort(byteBuffer);
        return new ZapUnsignedShort(value);
    }

    public Integer getValue() {
        return value;
    }
}
