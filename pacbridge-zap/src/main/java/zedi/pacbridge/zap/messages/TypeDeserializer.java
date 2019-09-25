package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.text.ParseException;

import org.jdom2.Element;

public interface TypeDeserializer<T> {
    public T deserialize(ByteBuffer byteBuffer, FieldDataType type);
    public T deserialize(Element element) throws ParseException;
}
