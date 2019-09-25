package zedi.pacbridge.net.auth;

import zedi.pacbridge.net.EncryptionType;

public class EncryptionContext {

    private EncryptionType encryptionType;
    private byte[] secretKey;
    private byte[] sessionKey;

    public EncryptionContext(EncryptionType encryptionType, byte[] secretKey, byte[] sessionKey) {
        this.encryptionType = encryptionType;
        this.secretKey = secretKey;
        this.sessionKey = sessionKey;
    }
    
    public EncryptionType encryptionType() {
        return encryptionType;
    }
    
    public byte[] getSessionKey() {
        return sessionKey;
    }
    
    public byte[] getSecretKey() {
        return secretKey;
    }
}