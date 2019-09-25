package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class GdnLong extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String TYPE_NAME = "Long";
    public static final String SHORT_TYPE_NAME = "LONG";
    public static final int TYPE_SIZE = 4;
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int MIN_VALUE = Integer.MIN_VALUE; 

    GdnLong() {
        super(GdnDataType.Long);
    }

    public GdnLong(Number value) {
        this();
        setValue(value.intValue());
    }

    public GdnLong(String stringValue) {
        this(new Integer(stringValue));
    }
    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer(byteBuffer.getInt()));
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(getValue().intValue());
    }
    

    @Override
    public Integer serializedSize() {
        return 4;
    }
    
    public static GdnLong gdnLongWithValue(Integer value) {
        return new GdnLong(value);
    }

    public static GdnLong longFromByteBufer(ByteBuffer byteBuffer) {
        return new GdnLong(byteBuffer.getInt());
    }
}
