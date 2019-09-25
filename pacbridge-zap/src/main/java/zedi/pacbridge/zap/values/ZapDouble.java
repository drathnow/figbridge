package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public class ZapDouble extends ZapValueBase implements ZapValue, Serializable {

    private Double value;
    
    protected ZapDouble() {
    }

    public ZapDouble(Number value) {
        super(ZapDataType.Double);
        this.value = value.doubleValue();
    }
        
    @Override
    public String toString() {
        return toString("#.######");
    }
    
    public String toString(String formatString) {
        return new DecimalFormat(formatString).format(value.doubleValue());
    }

    @Override
    public Integer serializedSize() {
        return Double.SIZE/8;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putDouble(value.doubleValue());
    }

    public static ZapDouble doubleFromByteBuffer(ByteBuffer byteBuffer) {
        Double value = byteBuffer.getDouble();
        return new ZapDouble(value);
    }

    public Double getValue() {
        return value;
    }
}
