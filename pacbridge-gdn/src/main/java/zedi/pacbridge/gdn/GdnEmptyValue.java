package zedi.pacbridge.gdn;

import java.nio.ByteBuffer;


public class GdnEmptyValue extends GdnValue<Object> {
    public static final String TYPE_NAME = "Empty Value";
    public static final String SHORT_TYPE_NAME = "Empty";
    public static final int TYPE_SIZE = 0;

    public GdnEmptyValue() {
        super(GdnDataType.EmptyValue);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
    }

    @Override
    public Integer serializedSize() {
        return 0;
    }

    public boolean isNumeric() {
        return false;
    }
}
