package zedi.pacbridge.zap.values;

import java.nio.ByteBuffer;
import java.text.ParseException;

public class ZapValueDeserializer {

    public ZapValue valueFromString(ZapDataType dataType, String string) throws ParseException {
        
        throw new IllegalArgumentException("Unknown ZAP Data Type number '" + dataType.toString() + "'");
    }
    
    public static ZapValue valueFromByteBuffer(ZapDataType dataType, ByteBuffer byteBuffer) {
        switch (dataType.getNumber()) {
            case ZapDataType.EMPTY_VALUE :
                return null;
            case ZapDataType.DISCRETE :
                return ZapDiscrete.descreteFromByteBuffer(byteBuffer);
            case ZapDataType.BYTE :
                return ZapByte.byteFromByteBuffer(byteBuffer);
            case ZapDataType.UNSIGNED_BYTE :
                return ZapUnsignedByte.unsignedByteFromByteBuffer(byteBuffer);
            case ZapDataType.INTEGER :
                return ZapShort.shortFromByteBuffer(byteBuffer);
            case ZapDataType.UNSIGNED_INTEGER :
                return ZapUnsignedShort.unsignedShortFromByteBuffer(byteBuffer);
            case ZapDataType.LONG :
                return ZapLong.longFromByteBuffer(byteBuffer);
            case ZapDataType.UNSIGNED_LONG :
                return ZapUnsignedLong.unsignedLongFromByteBuffer(byteBuffer);
            case ZapDataType.FLOAT :
                return ZapFloat.floatFromByteBuffer(byteBuffer);
            case ZapDataType.DOUBLE :
                return ZapDouble.doubleFromByteBuffer(byteBuffer);
            case ZapDataType.BINARY :
                return ZapBlob.blobFromByteBuffer(byteBuffer);
            case ZapDataType.STRING :
                return ZapString.stringFromByteBuffer(byteBuffer);
        }
        throw new IllegalArgumentException("Unknown ZAP Data Type number '" + dataType.toString() + "'");
    }
}
