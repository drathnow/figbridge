package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class ZapUnsignedLong extends ZapValueBase implements ZapValue, Serializable {

    private Long value;
    
    protected ZapUnsignedLong() {
    }
    
    public ZapUnsignedLong(Number value) {
        super(ZapDataType.UnsignedLong);
        this.value = value.longValue();
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

    public static ZapUnsignedLong unsignedLongFromByteBuffer(ByteBuffer byteBuffer) {
        long value = Unsigned.getUnsignedInt(byteBuffer);
        return new ZapUnsignedLong(value);
    }

    public Long getValue() {
        return value;
    }
}