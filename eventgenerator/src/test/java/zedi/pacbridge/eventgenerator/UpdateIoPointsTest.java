package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.ObjectType;

@Ignore
public class UpdateIoPointsTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleMissingId() throws Exception {
        testForMissingField("Id", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingSiteId() throws Exception {
        testForMissingField("SiteId", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleMissingTag() throws Exception {
        testForMissingField("Tag", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }
    
    @Test
    public void shouldHandleMissingPollSetId() throws Exception {
        testForMissingField("PollSetId", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingIOPointClass() throws Exception {
        testForMissingField("IOPointClass", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingDataType() throws Exception {
        testForMissingField("DataType", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingExternalDeviceId() throws Exception {
        testForMissingField("ExternalDeviceId", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingSourceAddress() throws Exception {
        testForMissingField("SourceAddress", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingSensorClassName() throws Exception {
        testForMissingField("SensorClassName", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingIsReadOnly() throws Exception {
        testForMissingField("IsReadOnly", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    @Test
    public void shouldHandleMissingAlarmMask() throws Exception {
        testForMissingField("AlarmMask", updateFields(), ActionType.UPDATE, ObjectType.IO_POINT);
    }

    private List<Field<?>> updateFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", 100));
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
