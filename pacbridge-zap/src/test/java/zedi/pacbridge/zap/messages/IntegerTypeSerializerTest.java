package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class IntegerTypeSerializerTest extends BaseTestCase {
    private static final Integer TAG_NUMBER = 0x0fff;
    private static final Integer VALUE = 123;
    
    private void assertCorrectEncoding(Short encodedNumber, int type) {
        assertEquals(type, (encodedNumber & 0xf000) >> 12);
        assertEquals(TAG_NUMBER.intValue(), encodedNumber & 0x0fff);
    }
    
    @Test
    public void shouldShouldSerializeS8Bits() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Byte.MAX_VALUE);
        
        assertEquals(3, byteBuffer.position());
        byteBuffer.flip();
        assertCorrectEncoding(byteBuffer.getShort(), FieldDataType.S8.getNumber());
        assertEquals(Byte.MAX_VALUE, byteBuffer.get());
    }

    @Test
    public void shouldShouldSerializeS16Bits() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Short.MAX_VALUE);
        
        assertEquals(4, byteBuffer.position());
        byteBuffer.flip();
        assertCorrectEncoding(byteBuffer.getShort(), FieldDataType.S16.getNumber());
        assertEquals(Short.MAX_VALUE, byteBuffer.getShort());
    }
    
    @Test
    public void shouldShouldSerializeS32Bits() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Integer.MAX_VALUE);
        
        assertEquals(6, byteBuffer.position());
        byteBuffer.flip();
        assertCorrectEncoding(byteBuffer.getShort(), FieldDataType.S32.getNumber());
        assertEquals(Integer.MAX_VALUE, byteBuffer.getInt());
    }
    
    @Test
    public void shouldShouldSerializeS48Bits() throws Exception {
        long value = 0x7ffffffffe0bL;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, value);
        
        assertEquals(8, byteBuffer.position());
        byteBuffer.flip();
        assertCorrectEncoding(byteBuffer.getShort(), FieldDataType.S48.getNumber());
        assertEquals(0x7f, byteBuffer.get());
        assertEquals((byte)0xff, (byte)byteBuffer.get());
        assertEquals((byte)0xff, (byte)byteBuffer.get());
        assertEquals((byte)0xff, (byte)byteBuffer.get());
        assertEquals((byte)0xfe, (byte)byteBuffer.get());
        assertEquals((byte)0x0b, (byte)byteBuffer.get());
    }
    
    @Test
    public void shouldShouldSerialize64Bits() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, Long.MAX_VALUE);
        
        assertEquals(10, byteBuffer.position());
        byteBuffer.flip();
        assertCorrectEncoding(byteBuffer.getShort(), FieldDataType.S64.getNumber());
        assertEquals(Long.MAX_VALUE, byteBuffer.getLong());
   }
    
    @Test
    public void shouldSerializeNumberToElement() throws Exception {
        Element element = new Element("foo");
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(element, VALUE.longValue());
        assertEquals(VALUE.toString(), element.getText());
    }
        
}
