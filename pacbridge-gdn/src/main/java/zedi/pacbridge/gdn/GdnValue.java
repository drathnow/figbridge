package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Value;

public abstract class GdnValue<TType> implements Value, GdnSerializable, Serializable {
    private static final long serialVersionUID = 1001L;
    
    private TType value;
    private boolean internal;
    private GdnDataType dataType;

    protected GdnValue(GdnDataType dataType, TType value) {
        this.dataType = dataType;
        this.internal = false;
        this.value = value;
    }
    
    protected GdnValue(GdnDataType dataType) {
        this.dataType = dataType;
        this.internal = false;
    }
    
    protected void setValue(TType value) {
        this.value = value;
    }
    
    public abstract boolean isNumeric();
    public abstract Integer serializedSize();
    
    public GdnDataType dataType() {
        return dataType;
    }
    
    public TType getValue() {
        return value;
    }
    
    public void setInternal(boolean isInternal) {
        this.internal = isInternal;
    }

    public boolean isInternal() {
        return internal;
    }

    public String toString() {
        return value.toString();
    }

    public static GdnValue<?> valueForDataType(GdnDataType dataType) {
        GdnValue<?> gdnValue;
        switch (dataType.getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE:
                gdnValue = new GdnDiscrete();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_BYTE:
                gdnValue = new GdnByte();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE:
                gdnValue = new GdnUnsignedByte();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER:
                gdnValue = new GdnInteger();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER:
                gdnValue = new GdnUnsignedInteger();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_LONG:
                gdnValue = new GdnLong();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG:
                gdnValue = new GdnUnsignedLong();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT:
                gdnValue = new GdnFloat();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_BLOB:
                gdnValue = new GdnBlob();
                break;
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE:
                gdnValue = new GdnEmptyValue();
                break;
            default:
                throw new IllegalArgumentException("Invalid number type specified: " + dataType.toString());
        }
        return gdnValue;
    }
    
    public static final GdnValue<?> valueFromByteBuffer(ByteBuffer byteBuffer, GdnDataType dataType) {
        if (dataType == GdnDataType.Discrete)
            return GdnDiscrete.discreteFromByteBuffer(byteBuffer);
        if (dataType == GdnDataType.Byte)
            return GdnByte.byteFromByteBufer(byteBuffer);
        if (dataType == GdnDataType.UnsignedByte)
            return GdnUnsignedByte.unsignedByteFromByteBufer(byteBuffer);
        if (dataType == GdnDataType.Integer)
            return GdnInteger.integerFromByteBufer(byteBuffer);
        if (dataType == GdnDataType.UnsignedInteger)
            return GdnUnsignedInteger.unsignedIntegerFromByteBufer(byteBuffer);
        if (dataType == GdnDataType.Long)
            return GdnLong.longFromByteBufer(byteBuffer);
        if (dataType == GdnDataType.UnsignedLong)
            return GdnUnsignedLong.unsignedLongFromByteBuffer(byteBuffer);
        if (dataType == GdnDataType.Binary)
            return GdnBlob.blobFromByteBuffer(byteBuffer);
        if (dataType == GdnDataType.Float)
            return GdnFloat.floatFromByteBuffer(byteBuffer);
        if (dataType == GdnDataType.EmptyValue)
            return new GdnEmptyValue();
        throw new IllegalArgumentException("Unrecognized data type: " + dataType.toString());
    }
}
