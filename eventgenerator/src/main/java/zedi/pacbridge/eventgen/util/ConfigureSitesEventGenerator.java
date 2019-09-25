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


public class ConfigureSitesEventGenerator extends BaseEventGenerator implements ConfigureEventGenerator {
    public static final Integer ADD_ID1 = 1;
    private static final Integer ID2 = 2;
    private static final String NAME1 = "Frumpy Bratwurst";
    private static final String NAME2 = "Spooge McGillicudy";

    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public ConfigureSitesEventGenerator(FieldTypeLibrary typeLibrary) {
        super(typeLibrary);
    }

    private List<Field<?>> addFields(String name) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", name));
        return fields;
    }

    private List<Field<?>> updateFields(Integer id, String name) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", id));
        fields.add(fieldForFieldNameAndValue("Name", name));
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
        actions.add(deleteAction(ADD_ID1));
        actions.add(addAction(NAME1));
        actions.add(updateAction(ADD_ID1, NAME1));
        actions.add(deleteAction(ID2));
        actions.add(addAction(NAME2));
        actions.add(updateAction(ADD_ID1, NAME2));
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), address, ObjectType.SITE, actions);
        return configureEvent;
    }
}
