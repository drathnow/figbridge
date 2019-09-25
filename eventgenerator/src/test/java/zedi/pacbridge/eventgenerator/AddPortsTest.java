package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.ObjectType;


public class AddPortsTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleSuccessfulAdd() throws Exception {
        testSuccessfulAddAction(addFields(), ObjectType.PORT);
    }
        
    @Test
    public void shouldHandleMissingPortType() throws Exception {
        testForMissingField("PortType", addFields(), ActionType.ADD, ObjectType.PORT);
    }
    
    @Test
    public void shouldHandleMissingName() throws Exception {
        testForMissingField("Name", addFields(), ActionType.ADD, ObjectType.PORT);
    }
    
    @Test
    public void shouldHandleMissingParameters() throws Exception {
        testForMissingField("Parameters", addFields(), ActionType.ADD, ObjectType.PORT);
    }

    @Test
    public void shouldHandleMissingPassThroughPort() throws Exception {
        testForMissingField("PassThroughPort", addFields(), ActionType.ADD, ObjectType.PORT);
    }

    @Test
    public void shouldHandleMissingPassThroughPortTimeout() throws Exception {
        testForMissingField("PassThroughPortTimeout", addFields(), ActionType.ADD, ObjectType.PORT);
    }

    @Test
    public void shouldHandleMissingModeControl() throws Exception {
        testForMissingField("ModeControl", addFields(), ActionType.ADD, ObjectType.PORT);
    }

    @Test
    public void shouldHandleMissingStartDelay() throws Exception {
        testForMissingField("StartDelay", addFields(), ActionType.ADD, ObjectType.PORT);
    }

    @Test
    public void shouldHandleMissingSensorEndDelay() throws Exception {
        testForMissingField("EndDelay", addFields(), ActionType.ADD, ObjectType.PORT);
    }
    
    @Test
    public void shouldHandleInvalidPortType() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("PortType");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, 99);
        List<Field<?>> fields = replaceFieldInList(field, addFields());
        testForInvalidValueField("PortType", fields, ActionType.ADD, ObjectType.PORT);
    }
    
    @Test
    public void shouldHandleInvalidModeControl() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("ModeControl");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, 99);
        List<Field<?>> fields = replaceFieldInList(field, addFields());
        testForInvalidValueField("ModeControl", fields, ActionType.ADD, ObjectType.PORT);
    }

    private List<Field<?>> addFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("PortType", 1));
        fields.add(fieldForFieldNameAndValue("Name", "Bar"));
        fields.add(fieldForFieldNameAndValue("Parameters", "Hello World"));
        fields.add(fieldForFieldNameAndValue("PassThroughPort", 1));
        fields.add(fieldForFieldNameAndValue("PassThroughPortTimeout", 8));
        fields.add(fieldForFieldNameAndValue("ModeControl", 1));
        fields.add(fieldForFieldNameAndValue("StartDelay", 1234));
        fields.add(fieldForFieldNameAndValue("EndDelay", 5678));
        return fields;
    }
}
