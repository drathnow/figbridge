package zedi.pacbridge.app.auth;

public interface SessionKeyGenerator {

    public abstract byte[] newSessionKeyForSecretKey(byte[] secretKey);

}