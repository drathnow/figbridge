package zedi.pacbridge.utl;

import java.nio.ByteBuffer;



public class HexStringEncoder {
    protected static final char HEX_CHARS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytesAsHexString(byte[] byteArray, int offset, int length, char aSeparator) {
        StringBuffer stringBuffer = new StringBuffer();
        if (aSeparator != ' ')
            stringBuffer.append(aSeparator);
        int len = 0;
        for (int i = offset; len < length; i++) {
            stringBuffer.append(HEX_CHARS[((byteArray[i] >> 4) & 0xF)]);
            stringBuffer.append(HEX_CHARS[((byteArray[i] >> 0) & 0xF)]);
            stringBuffer.append(aSeparator);
            len++;
        }
        return stringBuffer.toString();
    }
    
    public static String bytesAsHexString(byte[] someBytes) {
        return bytesAsHexString(someBytes, 0, someBytes.length);
    }

    public static String bytesAsHexString(ByteBuffer byteBuffer, char separator) {
        return bytesAsHexString(byteBuffer.array(), 0, byteBuffer.limit(), separator);
    }
    
    public static String bytesAsHexString(ByteBuffer byteBuffer) {
        return bytesAsHexString(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit()-byteBuffer.position(), ' ');
    }

    public static String bytesAsHexString(byte[] someBytes, int length) {
        return bytesAsHexString(someBytes, 0, length, ' ');
    }
    
    public static String bytesAsHexString(byte[] someBytes, int length, char separator) {
        return bytesAsHexString(someBytes, 0, length, separator);
    }

    public static String bytesAsHexString(byte[] someBytes, int offset, int length) {
        return bytesAsHexString(someBytes, offset, length, ' ');
    }
}
