package zedi.pacbridge.utl.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MessageDigestHasher implements Hasher {
    private MessageDigest digest;
    private int iterations;
    
    protected MessageDigestHasher(String hashType, int iterations) {
        this.iterations = iterations;
        try {
            digest = MessageDigest.getInstance(hashType);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to create "+ hashType + " hasher", e);
        }        
    }
    
    @Override
    public void update(byte[] bytes) {
        if (bytes != null && bytes.length > 0)
            digest.update(bytes);
    }

    @Override
    public void reset() {
        digest.reset();
    }
    
    @Override
    public byte[] hashedValue() {
        byte[] lastHash = digest.digest();
        for (int i = 1; i < iterations; i++) {
            digest.reset();
            digest.update(lastHash);
            lastHash = digest.digest();
        }
        return lastHash;
    }
    
    public byte[] hashedValue(int hashLength) {
        byte[] lastHash = digest.digest();
        for (int i = 1; i < iterations; i++) {
            digest.reset();
            digest.update(lastHash);
            lastHash = digest.digest();
        }
        
        if (lastHash.length == hashLength)
            return lastHash;
        byte[] result = new byte[hashLength];
        System.arraycopy(lastHash, 0, result, 0, hashLength);
        return result;
    }
    
}
