package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class FieldTest extends BaseTestCase {
    private static final Integer CORRELATION_ID = 65535;
    private static final Integer ID = 456;
    private static final Integer ERROR_CODE = 789;
    private static final String NAME = "FooManChoo";
    private static final String STRING = "Hello World";
    
    private static final FieldType CorrelationId = new FieldType("CorrelationId", 1, FieldDataType.S64); 
    private static final FieldType Id = new FieldType("Id", 2, FieldDataType.S32); 
    private static final FieldType ErrorCode = new FieldType("ErroCode", 3, FieldDataType.S32); 
    private static final FieldType Name = new FieldType("Name", 4, FieldDataType.STRING); 
    
    private static final Element CORRELATION_ID_ELEMENT = new Element(CorrelationId.getName()).setText(CORRELATION_ID.toString());
    private static final Element ID_ELEMENT = new Element(Id.getName()).setText(ID.toString());
    private static final Element ERROR_CODE_ELEMENT = new Element(ErrorCode.getName()).setText(ERROR_CODE.toString());
    private static final Element NAME_ELEMENT = new Element(Name.getName()).setText(NAME);
    
    @Test
    public void shouldSkipOverStringBytesIfTagIsUnknown() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForTag(CorrelationId.getTag())).willReturn(CorrelationId);
        int expectedLength = 4+STRING.length();
        ByteBuffer byteBuffer = ByteBuffer.allocate(expectedLength);
        short type = TypeNumberEncoder.encodedNumberFor(FieldDataType.STRING_NUMBER, 9999);
        byteBuffer.putShort(type);
        byteBuffer.putShort((short)STRING.length());
        byteBuffer.put(STRING.getBytes());
        byteBuffer.flip();
        Field<?> field = Field.fieldFromByteBuffer(byteBuffer, library);
        assertNull(field);
        assertEquals(expectedLength, byteBuffer.position());
    }
    
    @Test
    public void shouldSkipOverBytesIfTagIsUnknown() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForTag(CorrelationId.getTag())).willReturn(CorrelationId);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        short type = TypeNumberEncoder.encodedNumberFor(FieldDataType.S16_NUMBER, 9999);
        byteBuffer.putShort(type);
        byteBuffer.putShort(CORRELATION_ID.shortValue());
        byteBuffer.flip();
        Field<?> field = Field.fieldFromByteBuffer(byteBuffer, library);
        assertNull(field);
        assertEquals(4, byteBuffer.position());
    }

    @Test
    public void shouldDeserializeFieldFromByteBuffer() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForTag(CorrelationId.getTag())).willReturn(CorrelationId);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        short type = TypeNumberEncoder.encodedNumberFor(FieldDataType.S16_NUMBER, CorrelationId.getNumber());
        byteBuffer.putShort(type);
        byteBuffer.putShort(CORRELATION_ID.shortValue());
        byteBuffer.flip();
        Field<?> field = Field.fieldFromByteBuffer(byteBuffer, library);
        assertNotNull(field);
        assertEquals(CorrelationId.getName(), field.getFieldType().getName());
    }
    
    @Test
    public void shouldReturnCorrelationIdFromElement() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForName(CorrelationId.getName())).willReturn(CorrelationId);
        Field<?> field = Field.fieldForElement(CORRELATION_ID_ELEMENT, library);
        assertEquals(CorrelationId, field.getFieldType());
    }

    @Test
    public void shouldReturnIdFromElement() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForName(Id.getName())).willReturn(Id);
        Field<?> field = Field.fieldForElement(ID_ELEMENT, library);
        assertEquals(Id, field.getFieldType());
    }
    
    @Test
    public void shouldReturnErrorCodeFromElement() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForName(ErrorCode.getName())).willReturn(ErrorCode);
        Field<?> field = Field.fieldForElement(ERROR_CODE_ELEMENT, library);
        assertEquals(ErrorCode, field.getFieldType());
    }

    @Test
    public void shouldReturnNameFromElement() throws Exception {
        FieldTypeLibrary library = mock(FieldTypeLibrary.class);
        given(library.fieldTypeForName(Name.getName())).willReturn(Name);
        Field<?> field = Field.fieldForElement(NAME_ELEMENT, library);
        assertEquals(Name, field.getFieldType());
    }
    
}
