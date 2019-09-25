package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class GdnFloat extends GdnNumericValue<Float> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String TYPE_NAME = "Float";
    public static final String SHORT_TYPE_NAME = "FLOAT";
    public static final int TYPE_SIZE = 4;

    GdnFloat() {
        super(GdnDataType.Float);
    }

    public GdnFloat(Number value) {
        this();
        setValue(value.floatValue());
    }

    public GdnFloat(String stringValue) {
        this(new Float(stringValue));
    }

    
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        setValue(byteBuffer.getFloat());
    }

    @Override
    public Integer serializedSize() {
        return 4;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putFloat(getValue().floatValue());
    }

    public static GdnFloat floatFromByteBuffer(ByteBuffer byteBuffer) {
        return new GdnFloat(byteBuffer.getFloat());
    }

}
