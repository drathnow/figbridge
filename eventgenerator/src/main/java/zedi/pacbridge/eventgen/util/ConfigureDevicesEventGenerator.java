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

public class ConfigureDevicesEventGenerator extends BaseEventGenerator implements ConfigureEventGenerator {
    private static final Integer ID1 = 300;
    private static final String NAME1 = "Freddy Zipplemier";
    private static final Integer RTU_ADDR1 = 123;
    private static final Integer PORT_ID1= 1587;

    private static final Integer ID2 = 400;
    private static final String NAME2 = "Rupert McGillicuddy";
    private static final Integer RTU_ADDR2 = 456;
    private static final Integer PORT_ID2= 9872;
    private static final String PROTOCOL_ARGS = "{\"Arg1\": \"foo1\", \"Arg2\": \"bar1\"}";

    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public ConfigureDevicesEventGenerator(FieldTypeLibrary typeLibrary) {
        super(typeLibrary);
    }

    @Override
    public ConfigureEvent eventForSiteAddress(SiteAddress address) {
        List<Action> actions = new ArrayList<>();
        actions.add(deleteAction(ID1));
        actions.add(addAction(NAME1, RTU_ADDR1, PORT_ID1, null));
        actions.add(updateAction(ID1, NAME1, RTU_ADDR1, PORT_ID1, null));
        actions.add(deleteAction(ID2));
        actions.add(addAction(NAME2, RTU_ADDR2, PORT_ID2, PROTOCOL_ARGS));
        actions.add(updateAction(ID1, NAME2, RTU_ADDR2, PORT_ID2, PROTOCOL_ARGS));
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), address, ObjectType.DEVICE, actions);
        return configureEvent;
    }

    private List<Field<?>> addFields(String name, Integer rtuAddress, Integer portId, String protocolArguments) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("RtuAddress", rtuAddress));
        fields.add(fieldForFieldNameAndValue("PortId", portId));
        fields.add(fieldForFieldNameAndValue("ProtocolHandlerName", "MODBUS"));
        if (protocolArguments != null)
            fields.add(fieldForFieldNameAndValue("ProtocolArguments", protocolArguments));
        return fields;
    }

    private List<Field<?>> updateFields(Integer id, String name, Integer rtuAddress, Integer portId, String protocolArguments) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", id));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("RtuAddress", rtuAddress));
        fields.add(fieldForFieldNameAndValue("PortId", portId));
        fields.add(fieldForFieldNameAndValue("ProtocolHandlerName", "MODBUS"));
        return fields;
    }

    protected Action addAction(String name, Integer rtuAddress, Integer portId, String protocolArguments) {
        return new Action(ActionType.ADD, addFields(name, rtuAddress, portId, protocolArguments));
    }

    protected Action updateAction(Integer id, String name, Integer rtuAddress, Integer portId, String protocolArguments) {
        return new Action(ActionType.UPDATE, updateFields(id, name, rtuAddress, portId, protocolArguments));
    }
}
