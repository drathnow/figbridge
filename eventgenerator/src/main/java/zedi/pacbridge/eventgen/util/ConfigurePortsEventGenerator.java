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


public class ConfigurePortsEventGenerator extends BaseEventGenerator implements ConfigureEventGenerator {
    private static final Integer ID1 = 1;
    private static final Integer ID2= 2;
    private static final String NAME1 = "Port1";
    private static final String NAME2 = "Port2";
    
    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public ConfigurePortsEventGenerator(FieldTypeLibrary typeLibrary) {
        super(typeLibrary);
    }
    
    private List<Field<?>> addFields(String name) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("Parameters", "{foo: \"Bar\"}"));
        fields.add(fieldForFieldNameAndValue("PortType", 1));
        fields.add(fieldForFieldNameAndValue("PassThroughPort", 65535));
        fields.add(fieldForFieldNameAndValue("PassThroughPortTimeout", 2));
        fields.add(fieldForFieldNameAndValue("ModeControl", 2));
        fields.add(fieldForFieldNameAndValue("StartDelay", 1000));
        fields.add(fieldForFieldNameAndValue("EndDelay", 2000));
        return fields;
    }    
    
    private List<Field<?>> updateFields(Integer id, String name) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", id));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("Parameters", "{foo: \"Bar\"}"));
        fields.add(fieldForFieldNameAndValue("PortType", 1));
        fields.add(fieldForFieldNameAndValue("PassThroughPort", 1));
        fields.add(fieldForFieldNameAndValue("PassThroughPortTimeout", 2));
        fields.add(fieldForFieldNameAndValue("ModeControl", 2));
        fields.add(fieldForFieldNameAndValue("StartDelay", 1000));
        fields.add(fieldForFieldNameAndValue("EndDelay", 2000));
        return fields;
    } 
    
    protected Action addAction(String name) {
        return new Action(ActionType.ADD, addFields(name));
    }
        
    protected Action updateAction(Integer id, String name) {
        return new Action(ActionType.UPDATE, updateFields(id, name));
    }

    public ConfigureEvent eventForSiteAddress(SiteAddress address) {
        List<Action> actions = new ArrayList<>();
        actions.add(deleteAction(ID1));
        actions.add(addAction(NAME1));
        actions.add(updateAction(ID1, NAME1));
        actions.add(deleteAction(ID2));
        actions.add(addAction(NAME2));
        actions.add(updateAction(ID1, NAME2));
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), address, ObjectType.PORT, actions);
        return configureEvent;
    }
    

}
