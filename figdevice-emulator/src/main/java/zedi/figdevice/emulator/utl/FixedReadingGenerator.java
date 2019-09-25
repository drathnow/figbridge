package zedi.figdevice.emulator.utl;

import java.nio.ByteBuffer;
import java.util.Random;

import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.zap.values.ZapBlob;
import zedi.pacbridge.zap.values.ZapByte;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapDiscrete;
import zedi.pacbridge.zap.values.ZapDouble;
import zedi.pacbridge.zap.values.ZapFloat;
import zedi.pacbridge.zap.values.ZapLong;
import zedi.pacbridge.zap.values.ZapShort;
import zedi.pacbridge.zap.values.ZapString;
import zedi.pacbridge.zap.values.ZapUnsignedByte;
import zedi.pacbridge.zap.values.ZapUnsignedShort;
import zedi.pacbridge.zap.values.ZapValue;


public class FixedReadingGenerator implements ValueGenerator {
    private static final Random random = new Random(System.currentTimeMillis());
    private ZapDataType dataType;

    protected FixedReadingGenerator(ZapDataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public ZapValue nextValue() {
        Number baseValue = random.nextDouble();
        switch (dataType.getNumber()) {
            case ZapDataType.DISCRETE :
                return new ZapDiscrete(true);
            case ZapDataType.BYTE :
                return new ZapByte(baseValue.byteValue());
            case ZapDataType.UNSIGNED_BYTE :
                return new ZapUnsignedByte(baseValue.intValue() % 255);
            case ZapDataType.INTEGER :
                return new ZapShort(baseValue.shortValue());
            case ZapDataType.UNSIGNED_INTEGER :
                return new ZapUnsignedShort(baseValue.intValue() % 65535);
            case ZapDataType.LONG :
                return new ZapUnsignedShort(baseValue.intValue());
            case ZapDataType.UNSIGNED_LONG :
                return new ZapUnsignedShort(baseValue.longValue() % 0xffffffff);
            case ZapDataType.FLOAT :
                return new ZapFloat(baseValue.floatValue());
            case ZapDataType.BINARY :
                return new ZapBlob(bytesForValue(baseValue));
            case ZapDataType.LONG_LONG :
                return new ZapLong(baseValue.longValue());
            case ZapDataType.DOUBLE :
                return new ZapDouble(baseValue.doubleValue());
            case ZapDataType.STRING :
                return new ZapString(stringForValue(baseValue));
            default:
                throw new IllegalArgumentException("Data type " + dataType.getName() + " not currently supported");
        }
    }
    
    private String stringForValue(Number baseValue) {
        return HexStringEncoder.bytesAsHexString(bytesForValue(baseValue));
    }

    private byte[] bytesForValue(Number baseValue) {
        byte[] bytes = new byte[16];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.putLong(baseValue.longValue());
        return bytes;
    }
}
