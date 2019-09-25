package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public interface MessageFactory<TMessage extends Message> {
    public TMessage messageFromByteBuffer(Integer messageNumber, ByteBuffer byteBuffer);
}
