package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class ZapDiscrete extends ZapValueBase implements ZapValue, Serializable {

    private Boolean value;
    
    protected ZapDiscrete() {
    }
    
    public ZapDiscrete(boolean value) {
        super(ZapDataType.Discrete);
        this.value = value;
    }
    
    public ZapDiscrete(Number value) {
        this(value.intValue() == 0 ? false : true);
    }
    
    @Override
    public String toString() {
        return value.booleanValue() ? "1" : "0";
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(value ? 1 : 0));
    }
    
    @Override
    public Integer serializedSize() {
        return Byte.SIZE/8;
    }
    
    public static ZapDiscrete descreteFromByteBuffer(ByteBuffer byteBuffer) {
        int value = byteBuffer.get();
        return new ZapDiscrete(value == 0 ? false : true);
    }

    public Boolean getValue() {
        return value;
    }
}
