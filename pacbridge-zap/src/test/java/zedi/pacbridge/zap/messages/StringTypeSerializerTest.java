package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class StringTypeSerializerTest extends BaseTestCase {
    private static final String STRING = "FooManChoo";
    private static final Integer TAG_NUMBER = 20;
    
    @Test
    public void shouldSerializeStringToElement() throws Exception {
        Element element = new Element("foo");
        StringTypeSerializer serializer = new StringTypeSerializer();
        serializer.serialize(element, STRING);
        assertEquals(STRING, element.getText());
    }
    
    @Test
    public void shouldSerializeStringToByteBufferAsCountedByteString() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(STRING.length()+((Short.SIZE/8)*2));
        StringTypeSerializer serializer = new StringTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, STRING);
        byteBuffer.flip();
        short value = byteBuffer.getShort();
        assertEquals(TAG_NUMBER, TypeNumberEncoder.tagNumberFromEncodedValue(value));
        assertEquals(FieldDataType.STRING.getNumber(), TypeNumberEncoder.typeNumberFromEncodedValue(value));
        int len = byteBuffer.getShort();
        assertEquals(STRING.length(), len);
        byte[] bytes = new byte[len];
        byteBuffer.get(bytes);
        assertEquals(STRING, new String(bytes));
    }
}
