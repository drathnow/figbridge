package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jdom2.Element;

public class StringTypeSerializer implements TypeSerializer<String>, Serializable {

    private static final Integer FIXED_SIZE = 4;
    @Override
    public void serialize(ByteBuffer byteBuffer, int tagNumber, String value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.STRING.getNumber(), tagNumber));
        byteBuffer.putShort((short)value.length());
        byteBuffer.put(value.getBytes());
    }

    @Override
    public void serialize(Element element, String value) {
        element.setText(value);
    }
    
    @Override
    public Integer serializedSizeOfValue(String value) {
        return value.length() + FIXED_SIZE;
    }
}
