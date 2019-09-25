package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class IntegerTypeDeserializerTest extends BaseTestCase {
    private static final Integer TAG_NUMBER = 12;
    
    @Test
    public void shouldDeserializeS64() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, Long.MAX_VALUE);
        
        IntegerTypeDeserializer deserializer = new IntegerTypeDeserializer();
        
        byteBuffer.flip();
        byteBuffer.getShort();
        assertEquals(Long.MAX_VALUE, deserializer.deserialize(byteBuffer, FieldDataType.S64).longValue());
    }
    
    @Test
    public void shouldDeserializeS48() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, IntegerTypeSerializer.MAX_LONG48);
        
        IntegerTypeDeserializer deserializer = new IntegerTypeDeserializer();
        
        byteBuffer.flip();
        byteBuffer.getShort();
        assertEquals(IntegerTypeSerializer.MAX_LONG48.longValue(), deserializer.deserialize(byteBuffer, FieldDataType.S48).longValue());
    }
    
    @Test
    public void shouldDeserializeS32() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Integer.MAX_VALUE);
        
        IntegerTypeDeserializer deserializer = new IntegerTypeDeserializer();
        
        byteBuffer.flip();
        byteBuffer.getShort();
        assertEquals(Integer.MAX_VALUE, deserializer.deserialize(byteBuffer, FieldDataType.S32).intValue());
    }

    @Test
    public void shouldDeserializeS16() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Short.MAX_VALUE);
        
        IntegerTypeDeserializer deserializer = new IntegerTypeDeserializer();
        
        byteBuffer.flip();
        byteBuffer.getShort();
        assertEquals(Short.MAX_VALUE, deserializer.deserialize(byteBuffer, FieldDataType.S16).shortValue());
    }
    
    @Test
    public void shouldDeserializeS8() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer, TAG_NUMBER, (long)Byte.MAX_VALUE);
        
        IntegerTypeDeserializer deserializer = new IntegerTypeDeserializer();
        
        byteBuffer.flip();
        byteBuffer.getShort();
        assertEquals(Byte.MAX_VALUE, deserializer.deserialize(byteBuffer, FieldDataType.S8).byteValue());
    }
    
}
