package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

import zedi.pacbridge.utl.io.Unsigned;

public class ZapBlob extends ZapValueBase implements ZapValue, Serializable {

    private byte[] value;
    
    protected ZapBlob() {
    }
    
    public ZapBlob(byte[] value) {
        super(ZapDataType.Binary);
        this.value = value;
    }
    
    public byte[] getValue() {
        byte[] returnValue = new byte[value.length];
        System.arraycopy(value, 0, returnValue, 0, value.length);
        return returnValue;
    }
    
    @Override
    public String toString() {
        if (value != null)
            return new String(Base64.encodeBase64(value));
        return "";
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        if (value != null) {
            byteBuffer.putInt(value.length);
            byteBuffer.put(value);
        } else
            byteBuffer.putInt(0);
    }
    
    @Override
    public Integer serializedSize() {
        return value == null ? 0 : value.length + (Integer.SIZE/8);
    }
    
    public static ZapBlob blobFromByteBuffer(ByteBuffer byteBuffer) {
        long length = Unsigned.getUnsignedInt(byteBuffer);
        byte[] value = new byte[(int)length];
        byteBuffer.get(value);
        return new ZapBlob(value);
    }    
}
