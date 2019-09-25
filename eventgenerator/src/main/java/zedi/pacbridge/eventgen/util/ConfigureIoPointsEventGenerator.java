package zedi.pacbridge.eventgen.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import zedi.pacbridge.app.events.zios.ConfigureEvent;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureIoPointsEventGenerator extends BaseEventGenerator implements ConfigureEventGenerator {
    private static final Integer SITE_ID1 = 100;
    private static final Integer SITE_ID2 = 101;
    private static final Integer IO_ID1 = 200;
    private static final Integer IO_ID2 = 201;
    
    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public ConfigureIoPointsEventGenerator(FieldTypeLibrary typeLibrary) {
        super(typeLibrary);
    }

    private List<Field<?>> addFields(Integer siteId) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("SiteId", ConfigureSitesEventGenerator.ADD_ID1));
        fields.add(fieldForFieldNameAndValue("Tag", "IOTag"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 42));
        fields.add(fieldForFieldNameAndValue("IOPointClass", 3));
        fields.add(fieldForFieldNameAndValue("DataType", 8));
        fields.add(fieldForFieldNameAndValue("ExternalDeviceId", 0));
        fields.add(fieldForFieldNameAndValue("SourceAddress", "EXT;MQ;GB;00:80:00:00:00:00:b1:03;DI1"));
        fields.add(fieldForFieldNameAndValue("SensorClassName", "5678"));
        fields.add(fieldForFieldNameAndValue("IsReadOnly", 0));
      fields.add(fieldForFieldNameAndValue("AlarmMask", 0));
//        fields.add(fieldForFieldNameAndValue("AlarmMask", 0xFF));
//        fields.add(fieldForFieldNameAndValue("AlarmSetHysteresis", 111));
//        fields.add(fieldForFieldNameAndValue("AlarmClearHysteresis", 222));
//        fields.add(fieldForFieldNameAndValue("LowSet", "2.1"));
//        fields.add(fieldForFieldNameAndValue("LowHysteresis", "2.2"));
//        fields.add(fieldForFieldNameAndValue("LowLowSet", "3.1"));
//        fields.add(fieldForFieldNameAndValue("LowLowHysteresis", "3.2"));
//        fields.add(fieldForFieldNameAndValue("HighHighSet", "4.1"));
//        fields.add(fieldForFieldNameAndValue("HighHighHysteresis", "4.2"));
//        fields.add(fieldForFieldNameAndValue("HighSet", "5.1"));
//        fields.add(fieldForFieldNameAndValue("HighHysteresis", "5.2"));
        return fields;
    }

    private List<Field<?>> updateFields(Integer id, Integer siteId) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", id));
        fields.add(fieldForFieldNameAndValue("SiteId", siteId));
        fields.add(fieldForFieldNameAndValue("Tag", "IOTag"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 42));
        fields.add(fieldForFieldNameAndValue("IOPointClass", 3));
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

    private Action addAction(Integer siteId) {
        return new Action(ActionType.ADD, addFields(siteId));
    }

    private Action updateAction(Integer id, Integer siteId) {
        return new Action(ActionType.UPDATE, updateFields(id, siteId));
    }

    public ConfigureEvent eventForSiteAddress(SiteAddress address) {
        List<Action> actions = new ArrayList<>();
        actions.add(deleteAction(IO_ID1));
        actions.add(addAction(SITE_ID1));
        actions.add(updateAction(IO_ID1, SITE_ID1));
        actions.add(deleteAction(IO_ID2));
        actions.add(addAction(SITE_ID2));
        actions.add(updateAction(IO_ID2, SITE_ID2));
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), address, ObjectType.IO_POINT, actions);
        return configureEvent;
    }
}