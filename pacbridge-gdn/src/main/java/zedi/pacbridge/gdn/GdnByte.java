
package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class GdnByte extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;

    public static final String TYPE_NAME = "Byte";
    public static final String SHORT_TYPE_NAME = "BYTE";
    public static final int TYPE_SIZE = 1;

    GdnByte() {
        super(GdnDataType.Byte);
    }

    protected GdnByte(GdnDataType dataType) {
        super(dataType);
    }
    
    public GdnByte(Number value) {
        this();
        setValue(value.intValue());
    }

    public GdnByte(String stringValue) {
        this(new Integer(stringValue));
    }
    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer(byteBuffer.get()));
    }


    @Override
    public Integer serializedSize() {
        return 1;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(getValue().byteValue());
    }

    public static final GdnByte byteFromByteBufer(ByteBuffer byteBuffer) {
        return new GdnByte((int)byteBuffer.get());
    }
}
