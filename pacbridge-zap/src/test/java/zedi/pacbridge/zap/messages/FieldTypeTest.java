package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class FieldTypeTest extends BaseTestCase {

    @Test
    public void shouldDetectTagWithNoName() {
        Element element = new Element(FieldType.ROOT_ELEMENT_NAME);
        element.setAttribute(FieldType.TAG_TAG, "1");
        element.setAttribute(FieldType.TYPE_TAG, "s32");

        try {
            FieldType.fieldTypeForElement(element);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing value for FieldType element", e.getMessage());
        }
    }
    
    @Test
    public void shouldDetectBadType() throws Exception {
        Element element = new Element(FieldType.ROOT_ELEMENT_NAME);
        element.setAttribute(FieldType.TAG_TAG, "1");
        element.setAttribute(FieldType.TYPE_TAG, "x11");
        element.setText("ErrorCode");
        
        try {
            FieldType.fieldTypeForElement(element);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid FieldType element named 'ErrorCode': type value is invalid: 'x11'", e.getMessage());
        }
    }
    
    @Test
    public void shouldDetectBadTag() throws Exception {
        Element element = new Element(FieldType.ROOT_ELEMENT_NAME);
        element.setAttribute(FieldType.TAG_TAG, "fd");
        element.setAttribute(FieldType.TYPE_TAG, "s32");
        element.setText("ErrorCode");
        
        try {
            FieldType.fieldTypeForElement(element);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid FieldType element named 'ErrorCode': tag value is invalid: 'fd'", e.getMessage());
        }
    }
    
    @Test
    public void shouldParseElement() throws Exception {
        Element element = new Element(FieldType.ROOT_ELEMENT_NAME);
        element.setAttribute(FieldType.TAG_TAG, "1");
        element.setAttribute(FieldType.TYPE_TAG, "s32");
        element.setText("ErrorCode");
        
        FieldType fieldType = FieldType.fieldTypeForElement(element);
        
        assertEquals(1, fieldType.getTag().intValue());
        assertEquals(FieldDataType.S32, fieldType.getDataType());
        assertEquals("ErrorCode", fieldType.getName());
    }
}
