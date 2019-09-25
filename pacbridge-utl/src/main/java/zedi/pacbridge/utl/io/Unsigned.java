package zedi.pacbridge.utl.io;

import java.nio.ByteBuffer;

public class Unsigned {
    private static final long LONG_MASK = 0xffffffffL;
    private static final int SHORT_MASK = 0xffff;
    private static final int BYTE_MASK = 0xff;

    public static short getUnsignedByte(ByteBuffer byteBuffer) {
        return ((short)(byteBuffer.get() & BYTE_MASK));
    }

    public static short getUnsignedByte(ByteBuffer byteBuffer, int position) {
        return ((short)(byteBuffer.get(position) & (short)BYTE_MASK));
    }

    public static int getUnsignedShort(ByteBuffer byteBuffer) {
        return (byteBuffer.getShort() & SHORT_MASK);
    }

    public static int getUnsignedShort(ByteBuffer byteBuffer, int position) {
        return (byteBuffer.getShort(position) & SHORT_MASK);
    }

    public static long getUnsignedInt(ByteBuffer byteBuffer) {
        return ((long)byteBuffer.getInt() & LONG_MASK);
    }

    public static long getUnsignedInt(ByteBuffer byteBuffer, int position) {
        return ((long)byteBuffer.getInt(position) & LONG_MASK);
    }
}
