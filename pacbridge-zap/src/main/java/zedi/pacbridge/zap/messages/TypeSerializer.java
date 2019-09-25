package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jdom2.Element;

public interface TypeSerializer<T> extends Serializable {
    public Integer serializedSizeOfValue(T value);
    public void serialize(ByteBuffer byteBuffer, int tagNumber, T value);
    public void serialize(Element element, T value);
}
