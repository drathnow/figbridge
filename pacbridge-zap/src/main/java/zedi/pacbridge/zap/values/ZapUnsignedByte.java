package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class ZapUnsignedByte extends ZapValueBase implements ZapValue, Serializable {

    private Short value;
    
    protected ZapUnsignedByte() {
    }

    public ZapUnsignedByte(Number value) {
        super(ZapDataType.UnsignedByte);
        this.value = value.shortValue();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(value.byteValue());
    }
    
    @Override
    public Integer serializedSize() {
        return Byte.SIZE/8;
    }

    public Integer getValue() {
        return value.intValue();
    }
    
    public static ZapUnsignedByte unsignedByteFromByteBuffer(ByteBuffer byteBuffer) {
        int value = Unsigned.getUnsignedByte(byteBuffer);
        return new ZapUnsignedByte(value);
    }
}
