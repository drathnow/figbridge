package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.CompressionType;
import zedi.pacbridge.net.EncryptionType;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.zap.ZapSerializable;


public interface ChallengeResponseMessage extends Message, ZapSerializable, Serializable {
    public String getFirmwareVersion();
    public EncryptionType getEncryptionType();
    public CompressionType getCompressionType();
    public Integer getDeviceTime();
    public byte[] getClientSalt();
    public byte[] getClientHash();
    public String getUsername();
    public void serialize(ByteBuffer byteBuffer);
    public Integer size();
}