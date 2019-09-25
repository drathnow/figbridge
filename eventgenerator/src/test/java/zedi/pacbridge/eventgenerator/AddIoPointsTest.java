package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.ObjectType;


public class AddIoPointsTest extends ConfigureBaseTest {
    
    @Test
    public void shouldHandleSuccessfulAdd() throws Exception {
        testSuccessfulAddAction(addFields(), ObjectType.IO_POINT);
    }
        
    @Test
    public void shouldHandleMissingSiteId() throws Exception {
        testForMissingField("SiteId", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleMissingTag() throws Exception {
        testForMissingField("Tag", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleMissingPollSetId() throws Exception {
        testForMissingField("PollSetId", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingIOPointClass() throws Exception {
        testForMissingField("IOPointClass", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingDataType() throws Exception {
        testForMissingField("DataType", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingExternalDeviceId() throws Exception {
        testForMissingField("ExternalDeviceId", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingSourceAddress() throws Exception {
        testForMissingField("SourceAddress", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingSensorClassName() throws Exception {
        testForMissingField("SensorClassName", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingIsReadOnly() throws Exception {
        testForMissingField("IsReadOnly", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingAlarmMask() throws Exception {
        testForMissingField("AlarmMask", addFields(), ActionType.ADD, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleInvalidIOPointClass() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("IOPointClass");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, 99);
        List<Field<?>> fields = replaceFieldInList(field, addFields());
        testForInvalidValueField("IOPointClass", fields, ActionType.ADD, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleInvalidDataType() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("DataType");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, 99);
        List<Field<?>> fields = replaceFieldInList(field, addFields());
        testForInvalidValueField("DataType", fields, ActionType.ADD, ObjectType.IO_POINT);
    }

    private List<Field<?>> addFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("SiteId", 1));
        fields.add(fieldForFieldNameAndValue("Tag", "IOTag"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 42));
        fields.add(fieldForFieldNameAndValue("IOPointClass", 1));
        fields.add(fieldForFieldNameAndValue("DataType", 8));
        fields.add(fieldForFieldNameAndValue("ExternalDeviceId", 5));
        fields.add(fieldForFieldNameAndValue("SourceAddress", "1234"));
        fields.add(fieldForFieldNameAndValue("SensorClassName", "5678"));
        fields.add(fieldForFieldNameAndValue("IsReadOnly", 0));
        fields.add(fieldForFieldNameAndValue("AlarmMask", 0xFF));
        fields.add(fieldForFieldNameAndValue("AlarmSetHysteresis", 111));
        fields.add(fieldForFieldNameAndValue("AlarmClearHysteresis", 222));
        fields.add(fieldForFieldNameAndValue("LowSet", "2.1"));
        fields.add(fieldForFieldNameAndValue("LowHysteresis", "2.2"));
        fields.add(fieldForFieldNameAndValue("LowLowSet", "3.1"));
        fields.add(fieldForFieldNameAndValue("LowLowHysteresis", "3.2"));
        fields.add(fieldForFieldNameAndValue("HighHighSet", "4.1"));
        fields.add(fieldForFieldNameAndValue("HighHighHysteresis", "4.2"));
        fields.add(fieldForFieldNameAndValue("HighSet", "5.1"));
        fields.add(fieldForFieldNameAndValue("HighHysteresis", "5.2"));
        return fields;
    }
}
