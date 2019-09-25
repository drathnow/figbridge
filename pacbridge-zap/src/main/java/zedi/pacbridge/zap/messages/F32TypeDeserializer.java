package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.jdom2.Element;

public class F32TypeDeserializer implements TypeDeserializer<Float>, Serializable {
    static DecimalFormat decimalFormat = new DecimalFormat("###,###,###,##0.00");

    @Override
    public Float deserialize(ByteBuffer byteBuffer, FieldDataType type) {
        return byteBuffer.getFloat();
    }

    @Override
    public Float deserialize(Element element) throws ParseException {
        return decimalFormat.parse(element.getText()).floatValue();
    }

}
