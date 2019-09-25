package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class GdnUnsignedInteger extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String TYPE_NAME = "Unsigned Integer";
    public static final String SHORT_TYPE_NAME = "UINT";
    public static final int TYPE_SIZE = 2;
    public static final int MAX_VALUE = 0xffff;
    public static final int MIN_VALUE = 0; 

    GdnUnsignedInteger() {
        super(GdnDataType.UnsignedInteger);
    }

    public GdnUnsignedInteger(Number value) {
        this();
        setValue(value.intValue());
    }

    public GdnUnsignedInteger(String stringValue) {
        this(new Integer(stringValue));
    }
    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer((int)Unsigned.getUnsignedShort(byteBuffer)));
    }
    

    @Override
    public Integer serializedSize() {
        return 2;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(getValue().shortValue());
    }

    public static GdnUnsignedInteger unsignedIntegerFromByteBufer(ByteBuffer byteBuffer) {
        return new GdnUnsignedInteger(Unsigned.getUnsignedShort(byteBuffer));
    }
}
