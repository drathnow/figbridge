package zedi.figbridge.monitor.utl;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.messages.ChallengeResponseMessageV1;

public class ChallengeResponseMessageBuilder {
    public static final Integer CLIENT_SALT_LENGTH = 16;
    public static final Integer HASH_LENGTH = 16;

    private Hasher hasher;
    private String username;
    private byte[] secretKey;
    
    public ChallengeResponseMessageBuilder(Hasher hasher, String username, byte[] secretKey) {
        this.hasher = hasher;
        this.username = username;
        this.secretKey = secretKey;
    }

    public ChallengeResponseMessageV1 newMessageWithServerSalt(byte[] serverSalt) {
        hasher.reset();
        hasher.update(username.getBytes());
        if (secretKey != null)
            hasher.update(secretKey);
        hasher.update(secretKey);
        hasher.update(serverSalt);

        byte[] clientHash = hasher.hashedValue(HASH_LENGTH);
        ChallengeResponseMessageV1 message = new ChallengeResponseMessageV1(EncryptionType.NONE,
                                                                        CompressionType.NONE,
                                                                        (int)(System.currentTimeMillis()/1000),
                                                                        secretKey,
                                                                        clientHash,
                                                                        username);
        return message;
    }
}
