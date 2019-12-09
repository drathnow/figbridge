package zedi.pacbridge.app.events;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.nio.Buffer;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.net.DataTypeFactory;
import zedi.pacbridge.net.Value;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.values.ZapDataType;


public class WriteValueElementTest extends BaseTestCase {
    private static final Integer INDEX = 42;
    private static final DataType DATA_TYPE = ZapDataType.Float;
    private static final Float VALUE = Float.valueOf(4.5f);

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingValue() throws Exception {
        DataTypeFactory dataTypeFactory = mock(DataTypeFactory.class);
        DataType dataType = mock(DataType.class);
        Value value = mock(Value.class);
        
        given(dataTypeFactory.dataTypeForName(DATA_TYPE.getName())).willReturn(dataType);
        given(dataType.valueForString(VALUE.toString())).willReturn(value);
        
        Element element = elementForTest();
        element.removeChild(WriteValueElement.DATA_TYPE_TAG);
        WriteValueElement.writeValueForElement(element, dataTypeFactory);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingIndex() throws Exception {
        DataTypeFactory dataTypeFactory = mock(DataTypeFactory.class);
        DataType dataType = mock(DataType.class);
        Value value = mock(Value.class);
        
        given(dataTypeFactory.dataTypeForName(DATA_TYPE.getName())).willReturn(dataType);
        given(dataType.valueForString(VALUE.toString())).willReturn(value);

        Element element = elementForTest();
        element.removeChild(WriteValueElement.INDEX_TAG);
        WriteValueElement.writeValueForElement(element, dataTypeFactory);
    }
    
    @Test(expected = InvalidEventFormatException.class)
    public void testMissingDataType() throws Exception {
        Element element = elementForTest();
        element.removeChild(WriteValueElement.DATA_TYPE_TAG);
        WriteValueElement.writeValueForElement(element, null);
    }
    
    @Test
    public void testConstructor() throws Exception {
        DataTypeFactory dataTypeFactory = mock(DataTypeFactory.class);
        DataType dataType = mock(DataType.class);
        Value value = ZapDataType.Float.valueForString(VALUE.toString());
        
        given(dataTypeFactory.dataTypeForName(DATA_TYPE.getName())).willReturn(dataType);
        given(dataType.valueForString(VALUE.toString())).willReturn(value);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        Element element = elementForTest();
        
        WriteValueElement writeValue = WriteValueElement.writeValueForElement(element, dataTypeFactory);
        
        assertEquals(DATA_TYPE, writeValue.getDataType());
        assertEquals(INDEX.longValue(), writeValue.getIndex().longValue());
        writeValue.getValue().serialize(byteBuffer);
        ((Buffer)byteBuffer).flip();
        assertEquals(VALUE.floatValue(), byteBuffer.getFloat(), 0.1f);
    }

    private Element elementForTest() {
        Element element = new Element(WriteValueElement.ROOT_ELEMENT_NAME);
        element.addContent(new Element(WriteValueElement.DATA_TYPE_TAG).setText(DATA_TYPE.getName()));
        element.addContent(new Element(WriteValueElement.INDEX_TAG).setText(INDEX.toString()));
        element.addContent(new Element(WriteValueElement.VALUE_TAG).setText(VALUE.toString()));
        return element;
    }
}
