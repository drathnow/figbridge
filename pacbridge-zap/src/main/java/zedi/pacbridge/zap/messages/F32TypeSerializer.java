package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

import org.jdom2.Element;

class F32TypeSerializer implements TypeSerializer<Float>, Serializable {
    static DecimalFormat  decimalFormat = new DecimalFormat("###,###,###,##0.00");

    @Override
    public void serialize(ByteBuffer byteBuffer, int tagNumber, Float value) {
        byteBuffer.putShort(TypeNumberEncoder.encodedNumberFor(FieldDataType.F32.getNumber(), tagNumber));
        byteBuffer.putFloat(value);
    }

    @Override
    public void serialize(Element element, Float value) {
        element.setText(decimalFormat.format(value));
    }

    @Override
    public Integer serializedSizeOfValue(Float value) {
        return Float.SIZE/8;
    }
}
