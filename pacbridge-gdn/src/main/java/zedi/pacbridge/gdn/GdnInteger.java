package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class GdnInteger extends GdnNumericValue<Integer> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String TYPE_NAME = "Integer";
    public static final String SHORT_TYPE_NAME = "INT";
    public static final int TYPE_SIZE = 2;
    public static final int MAX_VALUE = 32766;
    public static final int MIN_VALUE = -32767; 

    GdnInteger() {
        super(GdnDataType.Integer);
    }

    public GdnInteger(Number value) {
        this();
        setValue(value.intValue());
    }
    
    public GdnInteger(String stringValue) {
        this(new Integer(stringValue));
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(new Integer(byteBuffer.getShort()));
    }

    @Override
    public Integer serializedSize() {
        return 2;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)getValue().intValue());
    }

    public static GdnInteger integerFromByteBufer(ByteBuffer byteBuffer) {
        return new GdnInteger((int)byteBuffer.getShort());
    }
}
