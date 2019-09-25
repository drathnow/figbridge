package zedi.pacbridge.app.devices;

import java.nio.ByteBuffer;

public class DefaultSecretKeyDecoder implements SecretKeyDecoder {
    public byte[] secretKeyFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return bytes;
    }
}