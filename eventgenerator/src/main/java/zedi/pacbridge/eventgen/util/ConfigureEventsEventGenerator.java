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
import zedi.pacbridge.zap.messages.TimedEventType;

public class ConfigureEventsEventGenerator extends BaseEventGenerator implements ConfigureEventGenerator {

    private static final String NAME1 = "Event1";
    private static final String NAME2 = "Event2";
    private static final TimedEventType EVENT_TYPE = TimedEventType.Report;
    private static final Integer PID = 1;
    private static final Integer START_TIME = (int)(System.currentTimeMillis()/1000L);
    private static final Integer INTERVAL = 50;
    private static final Integer DURATION = 0;
    
    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public ConfigureEventsEventGenerator(FieldTypeLibrary typeLibrary) {
        super(typeLibrary);
    }
    
    @Override
    public ConfigureEvent eventForSiteAddress(SiteAddress address) {
        List<Action> actions = new ArrayList<>();
        actions.add(updateAction(NAME1, EVENT_TYPE, PID, START_TIME, INTERVAL, DURATION));
        actions.add(addAction(NAME2, EVENT_TYPE, PID, START_TIME, INTERVAL, DURATION));
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), address, ObjectType.EVENT, actions);
        return configureEvent;
    }

    private Action addAction(String name, TimedEventType eventType, Integer pid, Integer startTime, Integer interval, Integer duration) {
        return new Action(ActionType.ADD, addFields(name, eventType, pid, startTime, interval, duration));
    }
    
    private Action updateAction(String name, TimedEventType eventType, Integer pid, Integer startTime, Integer interval, Integer duration) {
        return new Action(ActionType.UPDATE, updateFields(1, name, eventType, pid, startTime, interval, duration));
    }

    private List<Field<?>> addFields(String name, TimedEventType eventType, Integer pid, Integer startTime, Integer interval, Integer duration) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
        fields.add(fieldForFieldNameAndValue("StartTime", startTime));
        fields.add(fieldForFieldNameAndValue("Interval", interval));
        fields.add(fieldForFieldNameAndValue("Duration", duration));
        fields.add(fieldForFieldNameAndValue("PollSetId", pid));
        return fields;
    }

    private List<Field<?>> updateFields(Integer id, String name, TimedEventType eventType, Integer pid, Integer startTime, Integer interval, Integer duration) {
        List<Field<?>> fields = addFields(name, eventType, pid, startTime, interval, duration);
        fields.add(fieldForFieldNameAndValue("Id", id));
        return fields;
    }

}
