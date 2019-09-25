package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class GdnUnsignedByte extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String TYPE_NAME = "Unsigned Byte";
    public static final String SHORT_TYPE_NAME = "UBYTE";
    public static final int TYPE_SIZE = 1;
    static final int MAX_VALUE = 256;
    static final int MIN_VALUE = 0; 

    GdnUnsignedByte() {
        super(GdnDataType.UnsignedByte);
    }

    public GdnUnsignedByte(Number value) {
        this();
        setValue(value.intValue());
    }
    
    public GdnUnsignedByte(String stringValue) {
        this(new Integer(stringValue));
    }
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer((int)Unsigned.getUnsignedByte(byteBuffer)));
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(getValue().byteValue());
    }
    
    @Override
    public Integer serializedSize() {
        return 1;
    }
    
    public static final GdnUnsignedByte unsignedByteFromByteBufer(ByteBuffer byteBuffer) {
        return new GdnUnsignedByte((int)Unsigned.getUnsignedByte(byteBuffer));
    }
}
