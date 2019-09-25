package zedi.pacbridge.zap;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import zedi.pacbridge.utl.io.Unsigned;

public class CountedString {
    
    public static String stringFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] bytes = bytesFromByteBuffer(byteBuffer);
        return bytes == null ? null : new String(bytes, Charset.forName("US-ASCII"));
    }

    public static byte[] bytesFromByteBuffer(ByteBuffer byteBuffer) {
        short size = Unsigned.getUnsignedByte(byteBuffer);
        byte[] bytes = null;
        if (size > 0) {
            bytes = new byte[size];
            byteBuffer.get(bytes);
        }
        return bytes;
    }

    public static void serializeStringToByteBuffer(String string, ByteBuffer byteBuffer) {
        byte[] bytes = string.getBytes();
        byteBuffer.put((byte)bytes.length);
        byteBuffer.put(bytes);
    }
    
    public static void serializeBytesToByteBuffer(byte[] bytes, ByteBuffer byteBuffer) {
        byteBuffer.put((byte)bytes.length);
        byteBuffer.put(bytes);
    }
}
