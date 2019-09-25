package zedi.pacbridge.utl.io;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Aes128Hasher {
    public static String algorithm = "AES";

    private Key key;
    private Cipher cipher;
    
    public Aes128Hasher(byte[] secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        this.key = new SecretKeySpec(secretKey, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    public byte[] encodedBytesForBytes(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public byte[] decodedBytesForEncodedBytes(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }
}
