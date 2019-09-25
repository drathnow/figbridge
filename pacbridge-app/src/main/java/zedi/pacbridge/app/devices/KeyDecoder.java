package zedi.pacbridge.app.devices;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

public class KeyDecoder implements Serializable {
    private static final long serialVersionUID = 1001L;

    public static final int DEFAULT_DECODER = 0;
    
    public byte[] decodedBytesForBase64EncodedBytes(byte[] encodedBytes) throws SecretKeyDecoderException {
        byte[] bytes = Base64.decodeBase64(encodedBytes);
        if (bytes.length < 4)
            throw new SecretKeyDecoderException("Decoded Base64 string contains too few characters");
            
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int typeNumber = byteBuffer.getInt();
        SecretKeyDecoder keyDecoder = null;
        switch (typeNumber) {
            case DEFAULT_DECODER : 
                keyDecoder = new DefaultSecretKeyDecoder();
                break;
                
            default :
                throw new SecretKeyDecoderException("Encoded key has invalid type number. " + typeNumber + " is unknown");
        }
        return keyDecoder.secretKeyFromByteBuffer(byteBuffer);
    }
}
