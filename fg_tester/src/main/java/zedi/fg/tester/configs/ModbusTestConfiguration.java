package zedi.fg.tester.configs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;

import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ModbusTestConfiguration extends BaseTestConfiguration
{
	public static final Integer PROTOCOL_ID_MODBUS = 1;
	public static final Integer PROTOCOL_ID_ROC = 2;
	public static final Integer PROTOCOL_ID_MODBUS_TCP = 3;
	public static final Integer REQUEST_TIMEOUT = 5;
	public static final Integer MAX_RETRIES = 2;
	public static final Integer RTU_BACKOFF_COUNT = 3;
	public static final Integer RTU_BACKOFF_TIMEOUT = 300;
	public static final Integer SLAVE_ADDR1 = 1;
	public static final Integer SLAVE_ADDR2 = 2;
	public static final Integer COM1 = 1;
	public static final Integer COM2 = 2;
	public static final Integer COM3 = 3;
	public static final Integer EVENT_ID = 40;
	public static final Integer DEVICE1_ID = 210;
	public static final Integer DEVICE2_ID = 211;
	public static final Integer SITE_ID = 15;
	public static final Integer IOID1 = 100;
	public static final Integer IOID2 = 101;
	public static final Integer IOID3 = 102;
	public static final Integer IOID4 = 103;
	public static final Integer IOID5 = 104;
	public static final Integer IOID6 = 105;
	public static final Integer IOID7 = 106;
	public static final Integer IOID8 = 107;
	public static final Integer IOID9 = 108;

	private FieldTypeLibrary fieldTypeLibrary;

	public ModbusTestConfiguration(FieldTypeLibrary fieldTypeLibrary)
	{
		this.fieldTypeLibrary = fieldTypeLibrary;
	}

	public List<ConfigureControl> configureControls() {
		List<ConfigureControl> configureControlsList = new ArrayList<ConfigureControl>();
		
		List<Action> actions = new ArrayList<Action>();
		addDeviceActionsToList(actions, ActionType.DELETE);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.DEVICE, actions));
		
		actions = new ArrayList<Action>();
		addSiteActionsToList(actions, ActionType.DELETE);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.SITE, actions));
		
		actions = new ArrayList<Action>();
		addIoPointActionsToList(actions, ActionType.DELETE);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, actions));
				
		actions = new ArrayList<Action>();
		addEventActionsToList(actions, ActionType.DELETE);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions));

		actions = new ArrayList<Action>();
		addDeviceActionsToList(actions, ActionType.ADD);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.DEVICE, actions));
		
		actions = new ArrayList<Action>();
		addSiteActionsToList(actions, ActionType.ADD);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.SITE, actions));

		actions = new ArrayList<Action>();
		addIoPointActionsToList(actions, ActionType.ADD);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, actions));
		
		actions = new ArrayList<Action>();
		addEventActionsToList(actions, ActionType.ADD);
		configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions));
		
		return configureControlsList;
	}
	
	private void addDeviceActionsToList(List<Action> actions, ActionType actionType) 
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", DEVICE1_ID));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("Name", "Dave's RTU"));
			fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
			fields.add(fieldForFieldNameAndValue("PortId", COM1));
			fields.add(fieldForFieldNameAndValue("Parameters", SLAVE_ADDR1.toString() + ":1"));
			fields.add(fieldForFieldNameAndValue("RequestTimeout", REQUEST_TIMEOUT));
			fields.add(fieldForFieldNameAndValue("MaxRetries", MAX_RETRIES));
			fields.add(fieldForFieldNameAndValue("RtuBackoffCount", RTU_BACKOFF_COUNT));
			fields.add(fieldForFieldNameAndValue("RtuBackoffTimeout", RTU_BACKOFF_TIMEOUT));
		}
		actions.add(new Action(actionType, fields));
		
		fields  = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", DEVICE2_ID));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("Name", "Dave's Other RTU"));
			fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
			fields.add(fieldForFieldNameAndValue("PortId", COM1));
			fields.add(fieldForFieldNameAndValue("Parameters", SLAVE_ADDR2.toString() + ":1"));
			fields.add(fieldForFieldNameAndValue("RequestTimeout", REQUEST_TIMEOUT));
			fields.add(fieldForFieldNameAndValue("MaxRetries", MAX_RETRIES));
			fields.add(fieldForFieldNameAndValue("RtuBackoffCount", RTU_BACKOFF_COUNT));
			fields.add(fieldForFieldNameAndValue("RtuBackoffTimeout", RTU_BACKOFF_TIMEOUT));
		}
		actions.add(new Action(actionType, fields));
	}
	
	private void addSiteActionsToList(List<Action> actions, ActionType actionType)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", SITE_ID));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("Name", "Dave's Site"));
		}
		actions.add(new Action(actionType, fields));
	}
	
	private void addEventActionsToList(List<Action> actions, ActionType actionType)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", EVENT_ID));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("Duration", 1000));
			fields.add(fieldForFieldNameAndValue("TimedEventType", 1));
			fields.add(fieldForFieldNameAndValue("Interval", 10));
			fields.add(fieldForFieldNameAndValue("Name", "Daves Not Here"));
			fields.add(fieldForFieldNameAndValue("PollSetId", 300));
			fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
		}
		actions.add(new Action(actionType, fields));
	}

	private void addIoPointActionsToList(List<Action> actions, ActionType actionType)
	{
		actions.add(addIoPointAction(actionType, IOID1, DEVICE1_ID, "MBS;41002;NONE", "U32", 7));
		actions.add(addIoPointAction(actionType, IOID2, DEVICE1_ID, "MBS;41004;NONE", "U16", 5));
		actions.add(addIoPointAction(actionType, IOID3, DEVICE1_ID, "MBS;41005;NONE", "Float_NONE", 8));
		actions.add(addIoPointAction(actionType, IOID4, DEVICE2_ID, "MBS;41007;3412", "Float_3412", 8));
		actions.add(addIoPointAction(actionType, IOID5, DEVICE2_ID, "MBS;41009;2143", "Float_2143", 8));
		actions.add(addIoPointAction(actionType, IOID6, 0, "MBS;41009;2143", "Orphan_105", 8));
		actions.add(addIoPointAction(actionType, IOID7, DEVICE2_ID, "MBS;41012", "UCHAR", 3));
		actions.add(addIoPointAction(actionType, IOID8, DEVICE2_ID, "MBS;41013", "DISCRETE-Holding", 1));
		actions.add(addIoPointAction(actionType, IOID9, DEVICE2_ID, "MBS;3", "DISCRETE-Coil", 1));
	}

	private Action addIoPointAction(ActionType actionType, Integer ioid, Integer externalDeviceId, String sourceAddress, String tag, Integer dataType)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", ioid));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("IOPointClass", 2));
			fields.add(fieldForFieldNameAndValue("ExternalDeviceId", externalDeviceId));
			fields.add(fieldForFieldNameAndValue("PollSetId", 300));
			fields.add(fieldForFieldNameAndValue("IsReadOnly", 0));
			fields.add(fieldForFieldNameAndValue("SensorClassName", "RTU"));
			fields.add(fieldForFieldNameAndValue("SiteId", 15));
			fields.add(fieldForFieldNameAndValue("SourceAddress", sourceAddress));
			fields.add(fieldForFieldNameAndValue("IsSystemPoint", 0));
			fields.add(fieldForFieldNameAndValue("Tag", tag));
			fields.add(fieldForFieldNameAndValue("DataType", dataType));
		}
		return new Action(actionType, fields);
	}

	
	private Field<?> fieldForFieldNameAndValue(String fieldName, Object value)
	{
		FieldType fieldType = fieldTypeLibrary.fieldTypeForName(fieldName);
		assert(fieldType != null);
		return Field.fieldForFieldTypeAndValue(fieldType, value);
	}
}
