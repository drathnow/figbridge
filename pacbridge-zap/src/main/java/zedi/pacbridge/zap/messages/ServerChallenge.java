package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.ZapMessageType;

public class ServerChallenge extends ZapMessage {

    private byte[] saltValue;
    
    public ServerChallenge(byte[] saltValue) {
        super(ZapMessageType.ServerChallenge);
        this.saltValue = saltValue;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)saltValue.length);
        byteBuffer.put(saltValue);
    }

    public byte[] getServerSaltValue() {
        byte[] value = new byte[saltValue.length];
        System.arraycopy(saltValue, 0, value, 0, saltValue.length);
        return value;
    }
    
    @Override
    public Integer size() {
        return saltValue.length;
    }
    
    public static final ServerChallenge serverChallengeFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] saltValue = new byte[byteBuffer.get()];
        byteBuffer.get(saltValue);
        return new ServerChallenge(saltValue);
    }
}