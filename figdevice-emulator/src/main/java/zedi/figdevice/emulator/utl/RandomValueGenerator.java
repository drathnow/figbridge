package zedi.figdevice.emulator.utl;

import java.util.Random;

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


public class RandomValueGenerator implements ValueGenerator {
    public static final Integer MAX_BYTES = 100;
    
    private Random random;
    private ZapDataType[] validDataTypes = new ZapDataType[]{ZapDataType.Discrete, 
                                                             ZapDataType.Byte, 
                                                             ZapDataType.UnsignedByte, 
                                                             ZapDataType.Integer, 
                                                             ZapDataType.UnsignedInteger, 
                                                             ZapDataType.Long, 
                                                             ZapDataType.UnsignedLong, 
                                                             ZapDataType.Float, 
                                                             ZapDataType.Double};

    public RandomValueGenerator() {
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    public ZapValue nextValue() {
        ZapDataType dataType = validDataTypes[Math.abs(random.nextInt() % validDataTypes.length)];
        switch (dataType.getNumber()) {
            case ZapDataType.DISCRETE :
                return new ZapDiscrete(random.nextBoolean());
            case ZapDataType.BYTE :
                return new ZapByte(random.nextInt() % 128);
            case ZapDataType.UNSIGNED_BYTE :
                return new ZapUnsignedByte(Math.abs(random.nextInt() % 255));
            case ZapDataType.INTEGER :
                return new ZapShort(random.nextInt() & Short.MAX_VALUE);
            case ZapDataType.UNSIGNED_INTEGER :
                return new ZapUnsignedShort(Math.abs(random.nextInt() % 65535));
            case ZapDataType.LONG :
                return new ZapLong(random.nextInt());
            case ZapDataType.UNSIGNED_LONG :
                return new ZapUnsignedShort(Math.abs(random.nextLong() % 0xffffffff));
            case ZapDataType.FLOAT :
                return new ZapFloat(random.nextFloat());
            case ZapDataType.BINARY :
                return new ZapBlob(randomBytes());
            case ZapDataType.LONG_LONG :
                return new ZapLong(random.nextLong());
            case ZapDataType.DOUBLE :
                return new ZapDouble(random.nextDouble());
            case ZapDataType.STRING :
                return new ZapString(randomString());
            default:
                throw new IllegalArgumentException("Data type " + dataType.getName() + " not currently supported");
        }
    }
    
    private String randomString() {
        return new String(randomBytes());
    }
    
    private byte[] randomBytes() {
        byte[] bytes = new byte[random.nextInt() % MAX_BYTES];
        random.nextBytes(bytes);
        return bytes;
    }
    
    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 20; i++)
            System.out.println("Value: " + Math.abs(random.nextInt() % 65535));
    }
}
