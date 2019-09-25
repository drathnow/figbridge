package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.zap.CountedString;
import zedi.pacbridge.zap.ZapMessageType;

public class ChallengeResponseMessageV1 extends ZapMessage implements ChallengeResponseMessage {
    public static final Integer FIXED_LENGTH = 5;
    
    private EncryptionType encryptionType;
    private CompressionType compressionType;
    private Integer deviceTime;
    private byte[] clientSalt;
    private byte[] clientHash;
    private String username;
    
    public ChallengeResponseMessageV1(EncryptionType encryptionType, CompressionType compressionType, Integer deviceTime, byte[] clientSalt, byte[] clientHash, String username) {
        super(ZapMessageType.ClientChallengeResponse);
        this.encryptionType = encryptionType;
        this.compressionType = compressionType;
        this.deviceTime = deviceTime;
        this.clientSalt = clientSalt;
        this.clientHash = clientHash;
        this.username = username;
    }

    @Override
    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    @Override
    public CompressionType getCompressionType() {
        return compressionType;
    }

    @Override
    public Integer getDeviceTime() {
        return deviceTime;
    }

    @Override
    public byte[] getClientSalt() {
        return clientSalt;
    }

    @Override
    public byte[] getClientHash() {
        return clientHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getFirmwareVersion() {
        return null;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(size().byteValue());
        byteBuffer.put(encryptionType.getNumber().byteValue());
        byteBuffer.put(compressionType.getNumber().byteValue());
        byteBuffer.putInt(deviceTime);
        CountedString.serializeBytesToByteBuffer(clientSalt, byteBuffer);
        CountedString.serializeBytesToByteBuffer(clientHash, byteBuffer);
        CountedString.serializeStringToByteBuffer(username, byteBuffer);
    }

    @Override
    public Integer size() {
        return FIXED_LENGTH + clientHash.length + clientSalt.length + username.length();
    }
    
    public static ChallengeResponseMessageV1 clientChallengeResponseFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        EncryptionType encryptionType = EncryptionType.encryptionTypeForNumber((int)byteBuffer.get());
        CompressionType compressionType = CompressionType.compressionTypeForNumber((int)byteBuffer.get());
        Integer deviceTime = byteBuffer.getInt();
        byte[] clientSalt = CountedString.bytesFromByteBuffer(byteBuffer);
        byte[] clientHash = new byte[(int)byteBuffer.get()];
        byteBuffer.get(clientHash);
        byte[] foo = new byte[(int)byteBuffer.get()];
        byteBuffer.get(foo);
        String username = new String(foo, Charset.forName("US-ASCII"));
        return new ChallengeResponseMessageV1(encryptionType, compressionType, deviceTime, clientSalt, clientHash, username);
    }

}