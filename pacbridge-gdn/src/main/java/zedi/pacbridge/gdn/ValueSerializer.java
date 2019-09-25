package zedi.pacbridge.gdn;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

public class ValueSerializer {
    
    public void serializeValueToByteBufferAndPad(ByteBuffer byteBuffer, GdnValue<?> gdnValue) {
        int bytesToPad = 0;
        switch (gdnValue.dataType().getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_BYTE :
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE :
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE :
                gdnValue.serialize(byteBuffer);
                bytesToPad = 3;
                break;
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT :
                gdnValue.serialize(byteBuffer);
                break;
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER :
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER :
                gdnValue.serialize(byteBuffer);
                bytesToPad = 2;
                break;
            case GdnDataType.NUMBER_FOR_TYPE_LONG :
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG :
                gdnValue.serialize(byteBuffer);
                break;
            default :
                throw new IllegalArgumentException("Unknown value type to pad: " + gdnValue.dataType().getNumber());
        }
        for (int i = 0; i < bytesToPad; i++)
            byteBuffer.put((byte)0);
    }
    
    public void serializeValueFromStreamAndSkipPadding(ByteBuffer byteBuffer, GdnValue<?> gdnValue) {
        int bytesToSkip = 0;
        switch (gdnValue.dataType().getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_BYTE :
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE :
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE :
                gdnValue.deserialize(byteBuffer);
                bytesToSkip = 3;
                break;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG :
            case GdnDataType.NUMBER_FOR_TYPE_LONG :
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT :
                gdnValue.deserialize(byteBuffer);
                break;
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER :
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER :
                gdnValue.deserialize(byteBuffer);
                bytesToSkip = 2;
                break;
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE :
                bytesToSkip = 4;
                break;
            default :
                throw new IllegalArgumentException("Unknown value type to pad: " + gdnValue.dataType().getNumber());
        }
        for (int i = 0; i < bytesToSkip; i++)
            byteBuffer.get();
    }
    
    public final GdnValue<?> gdnValueForString(String stringValue, GdnDataType dataType) throws IllegalArgumentException {
        if (stringValue == null || stringValue.length() == 0)
            return null;
        switch (dataType.getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE :
                return new GdnDiscrete(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_BYTE :
                return new GdnByte(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE :
                return new GdnUnsignedByte(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT :
                return new GdnFloat(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER :
                return new GdnInteger(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER :
                return new GdnUnsignedInteger(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_LONG :
                return new GdnLong(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG :
                return new GdnUnsignedLong(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_BLOB :
                return new GdnBlob(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE :
                return null;
            default :
                throw new IllegalArgumentException("Invalid value type: " + dataType.toString());
        }
    }

    public final Object valueObjectForType(String stringValue, GdnDataType dataType) throws IllegalArgumentException {
        if (stringValue == null || stringValue.length() == 0)
            return null;
        switch (dataType.getNumber()) {
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Integer(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_BYTE :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Integer(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Integer(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT :
                return new Float(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Integer(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Integer(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_LONG :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Long(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG :
                return containsDecimal(stringValue) ? (Object)new Float(stringValue) : new Long(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_BLOB :
                return byteArrayFromBase64BlobString(stringValue);
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE :
                return null;
            default :
                throw new IllegalArgumentException("Invalid value type: " + dataType.toString());
        }
    }    
    private boolean containsDecimal(String aStringValue) {
        return aStringValue.indexOf('.') != -1;
    }
    
    private final byte[] byteArrayFromBase64BlobString(String base64String) {
        return base64String != null ? Base64.decodeBase64(base64String.getBytes()) : null;
    }

}
