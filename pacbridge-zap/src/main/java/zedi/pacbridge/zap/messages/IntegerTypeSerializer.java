package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jdom2.Element;

class IntegerTypeSerializer implements TypeSerializer<Long>, Serializable {
    public static final Long MAX_LONG48 = 0x7fffffffffffL;
    private static final Integer LONG48_SIZE = 12; 
    private static final Integer FIXED_SIZE = 2;
    
    private static byte long5(long x) { return (byte)(x >> 40); }
    private static byte long4(long x) { return (byte)(x >> 32); }
    private static byte long3(long x) { return (byte)(x >> 24); }
    private static byte long2(long x) { return (byte)(x >> 16); }
    private static byte long1(long x) { return (byte)(x >>  8); }
    private static byte long0(long x) { return (byte)(x      ); }

    static void putLong(ByteBuffer bb, long x) {
        bb.put(long5(x));
        bb.put(long4(x));
        bb.put(long3(x));
        bb.put(long2(x));
        bb.put(long1(x));
        bb.put(long0(x));
    }

    public void serialize(ByteBuffer byteBuffer, int tagNumber, Long value) {
        if (value.longValue() <= MAX_LONG48)
            if (value.longValue() <= Integer.MAX_VALUE)
                if (value.longValue() <= Short.MAX_VALUE)
                    if (value.longValue() <= Byte.MAX_VALUE)
                        encodeS8(byteBuffer, tagNumber, value.byteValue());
                    else
                        encodeS16(byteBuffer, tagNumber, value.shortValue());
                else
                    encodeS32(byteBuffer, tagNumber, value.intValue());
            else
                encodeInt48(byteBuffer, tagNumber, value.longValue());
        else
            encodeInt64(byteBuffer, tagNumber, value.longValue());
    }

    public void serialize(Element element, Long value) {
        element.setText(value.toString());
    }
    
    @Override
    public Integer serializedSizeOfValue(Long value) {
        if (value.longValue() <= MAX_LONG48)
            if (value.longValue() <= Integer.MAX_VALUE)
                if (value.longValue() <= Short.MAX_VALUE)
                    if (value.longValue() <= Byte.MAX_VALUE)
                        return FIXED_SIZE + (Byte.SIZE/8);
                    else
                        return FIXED_SIZE + (Short.SIZE/8);
                else
                    return FIXED_SIZE + (Integer.SIZE/8);
            else
                return FIXED_SIZE + LONG48_SIZE;
        else
            return FIXED_SIZE + (Long.SIZE/8);
    }
    
    private void encodeS8(ByteBuffer byteBuffer, int tagNumber, Byte value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.S8.getNumber(), tagNumber));
        byteBuffer.put(value.byteValue());
    }

    private void encodeS16(ByteBuffer byteBuffer, int tagNumber, Short value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.S16.getNumber(), tagNumber));
        byteBuffer.putShort(value.shortValue());
    }

    private void encodeS32(ByteBuffer byteBuffer, int tagNumber, Integer value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.S32.getNumber(), tagNumber));
        byteBuffer.putInt(value.intValue());
    }

    private void encodeInt48(ByteBuffer byteBuffer, int tagNumber, Long value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.S48.getNumber(), tagNumber));
        putLong(byteBuffer, value);
    }

    private void encodeInt64(ByteBuffer byteBuffer, int tagNumber, Long value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.S64.getNumber(), tagNumber));
        byteBuffer.putLong(value);
    }
}
