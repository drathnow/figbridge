package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.ObjectType;


public class AddDevicesTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleSuccessfulAddWithProtocolArguments() throws Exception {
        testSuccessfulAddAction(addFields(), ObjectType.DEVICE);
    }
        
    @Test
    public void shouldHandleSuccessfulAddWithoutProtocolArguments() throws Exception {
        List<Field<?>> addFields = addFields();
        removeFieldWithNameFromFieldList("ProtocolArguments", addFields);
        testSuccessfulAddAction(addFields, ObjectType.DEVICE);
    }

    @Test
    public void shouldHandleMissingName() throws Exception {
        testForMissingField("Name", addFields(), ActionType.ADD, ObjectType.DEVICE);
    }
    
    @Test
    public void shouldHandleMissingRtuAddress() throws Exception {
        testForMissingField("RtuAddress", addFields(), ActionType.ADD, ObjectType.DEVICE);
    }
    
    @Test
    public void shouldHandleMissingPortId() throws Exception {
        testForMissingField("PortId", addFields(), ActionType.ADD, ObjectType.DEVICE);
    }

    @Test
    public void shouldHandleMissingProtocolHandlerName() throws Exception {
        testForMissingField("ProtocolHandlerName", addFields(), ActionType.ADD, ObjectType.DEVICE);
    }

    @Test
    public void shouldHandleInvalidProtocolArguments() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("ProtocolArguments");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, "Foo");
        List<Field<?>> fields = replaceFieldInList(field, addFields());
        testForInvalidValueField("ProtocolArguments", fields, ActionType.ADD, ObjectType.DEVICE);
    }

    private List<Field<?>> addFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", "FooManChoo"));
        fields.add(fieldForFieldNameAndValue("RtuAddress", 1234));
        fields.add(fieldForFieldNameAndValue("PortId", 42));
        fields.add(fieldForFieldNameAndValue("ProtocolHandlerName", "BOB"));
        fields.add(fieldForFieldNameAndValue("ProtocolArguments", "{\"Arg1\": \"foo1\", \"Arg2\": \"bar1\"}"));
        return fields;
    }
}
