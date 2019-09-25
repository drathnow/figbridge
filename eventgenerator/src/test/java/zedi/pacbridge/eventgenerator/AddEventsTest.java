package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.ObjectType;


public class AddEventsTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleSuccessfulAdd() throws Exception {
        testSuccessfulAddAction(updateFields(), ObjectType.EVENT);
    }

    @Test
    public void shouldHandleMissingTimedEventType() throws Exception {
        testForMissingField("TimedEventType", updateFields(), ActionType.UPDATE, ObjectType.EVENT);
    }

    @Test
    public void shouldHandleMissingStartTime() throws Exception {
        testForMissingField("StartTime", updateFields(), ActionType.UPDATE, ObjectType.EVENT);
    }
    
    @Test
    public void shouldHandleMissingInterval() throws Exception {
        testForMissingField("Interval", updateFields(), ActionType.UPDATE, ObjectType.EVENT);
    }
    
    @Test
    public void shouldHandleMissingDuration() throws Exception {
        testForMissingField("Duration", updateFields(), ActionType.UPDATE, ObjectType.EVENT);
    }

    @Test
    public void shouldHandleInvalidTimedEventType() throws Exception {
        FieldType fieldType = typeLibrary.fieldTypeForName("TimedEventType");
        Field<?> field = Field.fieldForFieldTypeAndValue(fieldType, 99);
        List<Field<?>> fields = replaceFieldInList(field, updateFields());
        testForInvalidValueField("TimedEventType", fields, ActionType.UPDATE, ObjectType.EVENT);
    }

    private List<Field<?>> updateFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("TimedEventType", 1));
        fields.add(fieldForFieldNameAndValue("StartTime", 1234));
        fields.add(fieldForFieldNameAndValue("Interval", 50));
        fields.add(fieldForFieldNameAndValue("Duration", 100));
        fields.add(fieldForFieldNameAndValue("PollSetId", 5));
        return fields;
    }
}
