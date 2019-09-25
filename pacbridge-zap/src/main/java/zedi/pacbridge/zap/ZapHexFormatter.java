package zedi.pacbridge.zap;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.HexStringEncoder;

public class ZapHexFormatter {
    
    public static String format(ZapSerializable serializable) {
        return format(serializable, 2048);
    }
    
    private static String format(ZapSerializable serializable, int bufferSize) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            serializable.serialize(byteBuffer);
            byteBuffer.flip();
            return HexStringEncoder.bytesAsHexString(byteBuffer);
        } catch (BufferOverflowException e) {
            if (bufferSize > 1E6)
                throw e;
            return format(serializable, bufferSize + 1000);
        }
    }
}
