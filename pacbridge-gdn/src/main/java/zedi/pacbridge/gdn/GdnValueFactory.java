package zedi.pacbridge.gdn;


public class GdnValueFactory {
    
    public GdnValue<?> valueForDataType(GdnDataType dataType) {
        return GdnValue.valueForDataType(dataType);
    }    

    public static GdnValue<?> gdnValueForDataType(GdnDataType dataType, Object value) {
        GdnValue<?> gdnValue;
        switch (dataType.getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE:
                gdnValue = new GdnDiscrete(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_BYTE:
                gdnValue = new GdnByte(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE:
                gdnValue = new GdnUnsignedByte(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER:
                gdnValue = new GdnInteger(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER:
                gdnValue = new GdnUnsignedInteger(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_LONG:
                gdnValue = new GdnLong(((Number)value).intValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG:
                gdnValue = new GdnUnsignedLong(((Number)value).longValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT:
                gdnValue = new GdnFloat(((Number)value).floatValue());
                break;
            case GdnDataType.NUMBER_FOR_TYPE_BLOB:
                gdnValue = new GdnBlob((byte[])value);
                break;
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE:
                gdnValue = new GdnEmptyValue();
                break;
            default:
                throw new IllegalArgumentException("Invalid number type specified: " + dataType.toString());
        }
        return gdnValue;
    }
}
