package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class ZapString extends ZapValueBase implements ZapValue, Serializable {

    private String value;
    
    protected ZapString() {
    }
    
    public ZapString(String value) {
        super(ZapDataType.String);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)value.length());
        byteBuffer.put(value.getBytes());
    }

    @Override
    public Integer serializedSize() {
        return value == null ? 0 : value.length()+(Short.SIZE/8);
    }
    
    public static ZapString stringFromByteBuffer(ByteBuffer byteBuffer) {
        int length = Unsigned.getUnsignedShort(byteBuffer);
        byte[] value = new byte[(int)length];
        byteBuffer.get(value);
        return new ZapString(new String(value));
    }
    

}
