package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.Test;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;


public class ConfigureResponseEventTest extends ZiosEventTestCase {
    private static final String CORRELATION_ID_KEY = "CorrelationId";
    private static final String ERROR_CODE_KEY = "ErrorCode";
    private static final String ID_KEY = "Id";
    
    private static final Long CORID1 = 123L;
    private static final Long CORID2 = 567L;
    private static final Long CORID3 = 2345L;
    private static final Long ERR_CODE = 0L;
    private static final Long ID = 10L;
    
    private static final Long COMMAND_ID = 1234L;
    private Map<Integer, FieldType> tagToFieldTypeMap;
    private Map<String, FieldType> nameToFieldTypeMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tagToFieldTypeMap = new TreeMap<Integer, FieldType>();
        nameToFieldTypeMap = new TreeMap<String, FieldType>();
        loadFieldTypes();
    }

    @Test
    public void shouldCreateCorrectXml() throws Exception {
        List<Field<?>> deleteFields1 = new ArrayList<Field<?>>();
        deleteFields1.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(CORRELATION_ID_KEY), CORID1));
        deleteFields1.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(ERROR_CODE_KEY), ERR_CODE));
        Action deleteAction1 = new Action(ActionType.DELETE, deleteFields1);
        
        List<Field<?>> deleteFields2 = new ArrayList<Field<?>>();
        deleteFields2.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(CORRELATION_ID_KEY), CORID2));
        deleteFields2.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(ERROR_CODE_KEY), ERR_CODE));
        Action deleteAction2 = new Action(ActionType.DELETE, deleteFields2);

        List<Field<?>> addFields = new ArrayList<Field<?>>();
        addFields.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(CORRELATION_ID_KEY), CORID2));
        addFields.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(ID_KEY), ID));
        addFields.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(ERROR_CODE_KEY), ERR_CODE));
        Action addAction = new Action(ActionType.DELETE, addFields);
        
        List<Field<?>> updateFields = new ArrayList<Field<?>>();
        updateFields.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(CORRELATION_ID_KEY), CORID3));
        updateFields.add(Field.fieldForFieldTypeAndValue(nameToFieldTypeMap.get(ERROR_CODE_KEY), ERR_CODE));
        Action updateAction = new Action(ActionType.DELETE, updateFields);

        List<Action> actions = Arrays.asList(deleteAction1, deleteAction2, addAction, updateAction);
        ConfigureResponseEvent event = new ConfigureResponseEvent(ObjectType.SITE, COMMAND_ID, actions, "foo");
        System.out.println(event.asXmlString());
        assertTrue(isValidXml(event.asXmlString()));
    }

    void loadFieldTypes() throws JDOMException {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        Element rootElement = JDomUtilities.elementForInputStream(inputStream);
        List<Element> fieldTypes = rootElement.getChildren("FieldType");
        for (Element element : fieldTypes) {
            FieldType fieldType = FieldType.fieldTypeForElement(element);
            tagToFieldTypeMap.put(fieldType.getTag(), fieldType);
            nameToFieldTypeMap.put(fieldType.getName(), fieldType);
        }
    }

}
