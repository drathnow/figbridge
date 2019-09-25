package zedi.pacbridge.zap;

import java.nio.ByteBuffer;
import java.util.Arrays;

import zedi.pacbridge.utl.io.Unsigned;

public class Blob {
    private byte[] bytes;
    
    private Blob() {
    }
    
    public Blob(byte[] bytes) {
        bytes = Arrays.copyOf(bytes, bytes.length);
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(bytes.length);
    }
    
    public static Blob blobFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[(int)Unsigned.getUnsignedInt(byteBuffer)];
        Blob blob = new Blob();
        blob.bytes = bytes;
        return blob;
    }
}