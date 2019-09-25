package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;

import org.jdom2.Element;

import zedi.pacbridge.utl.io.Unsigned;

public class StringTypeDeserializer implements TypeDeserializer<String>, Serializable  {

    @Override
    public String deserialize(ByteBuffer byteBuffer, FieldDataType type) {
        byte[] bytes = new byte[Unsigned.getUnsignedShort(byteBuffer)];
        byteBuffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public String deserialize(Element element) throws ParseException {
        return element.getText();
    }
}
