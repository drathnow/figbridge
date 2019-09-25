package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class GdnUnsignedLong extends GdnNumericValue<Long> implements Serializable {
    private static final long serialVersionUID = 1001L;

    public static final String TYPE_NAME = "Unsigned Long";
    public static final String SHORT_TYPE_NAME = "ULONG";
    public static final int TYPE_SIZE = 4;
    public static final long MAX_VALUE = 0xffffffffL;
    public static final long MIN_VALUE = 0; 

    GdnUnsignedLong() {
        super(GdnDataType.UnsignedLong);
    }

    public GdnUnsignedLong(Number value) {
        this();
        setValue(value.longValue());
    }
    
    public GdnUnsignedLong(String stringValue) {
        this(new Long(stringValue));
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Long(Unsigned.getUnsignedInt(byteBuffer)));
    }

    @Override
    public Integer serializedSize() {
        return 4;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt((int)getValue().longValue());
    }

    public static GdnUnsignedLong unsignedLongFromByteBuffer(ByteBuffer byteBuffer) {
        return new GdnUnsignedLong(Unsigned.getUnsignedInt(byteBuffer));
    }
}
