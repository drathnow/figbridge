package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public interface Message {
    public void serialize(ByteBuffer byteBuffer);
    public MessageType messageType();
    public Integer sequenceNumber();
    public void setSequenceNumber(Integer sequenceNumber);
}