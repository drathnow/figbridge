package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

public class GdnDiscrete extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    GdnDiscrete() {
        super(GdnDataType.Discrete);
    }

    public GdnDiscrete(Number value) {
        this();
        setValue(value.intValue());
    }

    public GdnDiscrete(String stringValue) {
        this(new Integer(stringValue));
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer(Unsigned.getUnsignedByte(byteBuffer)));
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(getValue().byteValue());
    }
 
    @Override
    public Integer serializedSize() {
        return 1;
    }
    
    public static final GdnDiscrete discreteFromByteBuffer(ByteBuffer byteBuffer) {
        return new GdnDiscrete((int)byteBuffer.get());
    }
}
