package zedi.pacbridge.app.devices;

import java.nio.ByteBuffer;


public interface SecretKeyDecoder {
    public abstract byte[] secretKeyFromByteBuffer(ByteBuffer byteBuffer);
}
