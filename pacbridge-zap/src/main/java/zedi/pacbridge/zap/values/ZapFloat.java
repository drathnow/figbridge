package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public class ZapFloat extends ZapValueBase implements ZapValue, Serializable {

    private Float value;
    
    protected ZapFloat() {
    }
    
    public ZapFloat(Number value) {
        super(ZapDataType.Float);
        this.value = value.floatValue();
    }
    
    @Override
    public String toString() {
        return toString("#.####");
    }
    
    public String toString(String formatString) {
        return new DecimalFormat(formatString).format(value.floatValue());
    }
    
    @Override
    public Integer serializedSize() {
        return Float.SIZE/8;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putFloat(value.floatValue());
    }

    public static ZapFloat floatFromByteBuffer(ByteBuffer byteBuffer) {
        Float value = byteBuffer.getFloat();
        return new ZapFloat(value);
    }

    public Float getValue() {
        return value;
    }
}
