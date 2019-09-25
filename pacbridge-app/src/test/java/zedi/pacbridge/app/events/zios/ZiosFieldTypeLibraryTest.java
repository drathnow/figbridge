package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.app.config.BridgeConfiguration;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.FieldType;

public class ZiosFieldTypeLibraryTest extends BaseTestCase {
    private static final Integer TAG = 88;
    private static final String NAME = "Foo";
    
    private static final String XML_TYPES = 
              "<FieldTypes>"
            + "    <FieldType tag='1' type='s64'>CorrelationId</FieldType>"
            + "    <FieldType tag='2' type='s32'>Id</FieldType>"
            + "</FieldTypes>";
    
    @Test
    public void shouldNotReplaceExistingName() throws Exception {
        BridgeConfiguration bridgeConfiguration = mock(BridgeConfiguration.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(XML_TYPES.getBytes());
        FieldType fieldType = mock(FieldType.class);

        List<FieldType> fieldTypes = new ArrayList<FieldType>();
        given(bridgeConfiguration.getFieldTypes()).willReturn(fieldTypes);
        given(fieldType.getTag()).willReturn(TAG);
        given(fieldType.getName()).willReturn("CorrelationId");
        fieldTypes.add(fieldType);
        
        ZiosFieldTypeLibrary library = new ZiosFieldTypeLibrary(bridgeConfiguration);
        library.loadFieldTypes(bais);

        assertNotSame(fieldType, library.fieldTypeForName("CorrelationId"));
        assertNotNull(library.fieldTypeForName("Id"));
        assertNotSame(fieldType, library.fieldTypeForTag(1));
        assertNotNull(library.fieldTypeForTag(2));
    }

    @Test
    public void shouldNotReplaceExistingTag() throws Exception {
        BridgeConfiguration bridgeConfiguration = mock(BridgeConfiguration.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(XML_TYPES.getBytes());
        FieldType fieldType = mock(FieldType.class);

        List<FieldType> fieldTypes = new ArrayList<FieldType>();
        given(bridgeConfiguration.getFieldTypes()).willReturn(fieldTypes);
        given(fieldType.getTag()).willReturn(1);
        given(fieldType.getName()).willReturn(NAME);
        fieldTypes.add(fieldType);
        
        ZiosFieldTypeLibrary library = new ZiosFieldTypeLibrary(bridgeConfiguration);
        library.loadFieldTypes(bais);

        assertNotSame(fieldType, library.fieldTypeForName("CorrelationId"));
        assertNotNull(library.fieldTypeForName("Id"));
        assertNotSame(fieldType, library.fieldTypeForTag(1));
        assertNotNull(library.fieldTypeForTag(2));
    }
    
    @Test
    public void shouldMergeWithBridgeConfigurationFieldTypes() throws Exception {
        BridgeConfiguration bridgeConfiguration = mock(BridgeConfiguration.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(XML_TYPES.getBytes());
        FieldType fieldType = mock(FieldType.class);

        List<FieldType> fieldTypes = new ArrayList<FieldType>();
        given(bridgeConfiguration.getFieldTypes()).willReturn(fieldTypes);
        given(fieldType.getTag()).willReturn(TAG);
        given(fieldType.getName()).willReturn(NAME);
        fieldTypes.add(fieldType);
        
        ZiosFieldTypeLibrary library = new ZiosFieldTypeLibrary(bridgeConfiguration);
        library.loadFieldTypes(bais);

        assertNotNull(library.fieldTypeForName("CorrelationId"));
        assertNotNull(library.fieldTypeForName("Id"));
        assertNotNull(library.fieldTypeForName("Foo"));
        assertNotNull(library.fieldTypeForTag(1));
        assertNotNull(library.fieldTypeForTag(2));
        assertNotNull(library.fieldTypeForTag(TAG));
    }
    
    @Test
    public void shouldParseFieldTypes() throws Exception {
        BridgeConfiguration bridgeConfiguration = mock(BridgeConfiguration.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(XML_TYPES.getBytes());

        ZiosFieldTypeLibrary library = new ZiosFieldTypeLibrary(bridgeConfiguration);
        library.loadFieldTypes(bais);
        
        assertNotNull(library.fieldTypeForName("CorrelationId"));
        assertNotNull(library.fieldTypeForName("Id"));
        assertNotNull(library.fieldTypeForTag(1));
        assertNotNull(library.fieldTypeForTag(2));
    }
}
