package zedi.pacbridge.app.auth;


public class DefaultSessionKeyGenerator implements SessionKeyGenerator {

    @Override
    public byte[] newSessionKeyForSecretKey(byte[] secretKey) {
        return null;
    }
}
