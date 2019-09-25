package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;

import org.jdom2.Element;

class IntegerTypeDeserializer implements TypeDeserializer<Long>, Serializable {

    public Long deserialize(ByteBuffer byteBuffer, FieldDataType type) {
        switch (type.getNumber()) {
            case FieldDataType.S8_NUMBER :
                return (long)byteBuffer.get();
            case FieldDataType.S16_NUMBER :
                return (long)byteBuffer.getShort();
            case FieldDataType.S32_NUMBER :
                return (long)byteBuffer.getInt();
            case FieldDataType.S48_NUMBER :
                return ((long)byteBuffer.getShort() << 32) | ((long)byteBuffer.getInt() & 0x00000000ffffffffL);
            case FieldDataType.S64_NUMBER :
                return byteBuffer.getLong();
        }
        throw new IllegalArgumentException("Deserialize for FieldType " + type.getName() + " is not implemented");
    }

    @Override
    public Long deserialize(Element element) throws ParseException {
        return new Long(element.getText());
    }
}
