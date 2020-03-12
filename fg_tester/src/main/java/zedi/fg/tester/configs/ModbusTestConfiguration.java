package zedi.fg.tester.configs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.log4j.Logger;

import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;
import zedi.pacbridge.zap.messages.TimedEventType;

public class ModbusTestConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
	private static final Logger logger = Logger.getLogger(ModbusTestConfiguration.class);

	public static final Integer PROTOCOL_ID_MODBUS = 1;
	public static final Integer PROTOCOL_ID_ROC = 2;
	public static final Integer PROTOCOL_ID_MODBUS_TCP = 3;
	public static final Integer REQUEST_TIMEOUT = 5000;
	public static final Integer MAX_RETRIES = 2;
	public static final Integer RTU_BACKOFF_COUNT = 3;
	public static final Integer RTU_BACKOFF_TIMEOUT = 300;
	public static final Integer SLAVE_ADDR1 = 1;
	public static final Integer SLAVE_ADDR2 = 2;
	public static final Integer COM1 = 1;
	public static final Integer COM2 = 2;
	public static final Integer COM3 = 3;
	public static final Integer DEVICE1_ID = 210;
	public static final Integer DEVICE2_ID = 211;

	enum State
	{
		INIT, ADD_DEVICE, ADD_SITE, ADD_IO_POINTS, ADD_EVENTS
	}

	private Deque<ConfigureControl> configureControlsList = new ArrayDeque<ConfigureControl>();
	private Integer siteId;
	private Integer deviceId1;
	private Integer deviceId2;
	private State currentState;

	public ModbusTestConfiguration(FieldTypeLibrary fieldTypeLibrary)
	{
		super(fieldTypeLibrary);
		this.siteId = 0;
		this.currentState = State.INIT;
	}

	@Override
	public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
	{
		if (ackDetails.getObjectType().getNumber() == ObjectType.SITE.getNumber())
		{
			List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
			if (actions.get(0).getActionType().getNumber() == ActionType.ADD.getNumber())
			{
				List<Field<?>> fields = actions.get(0).getFields();
				for (Field<?> field : fields)
				{
					if (field.getFieldType().getName().equals("Id"))
					{
						siteId = ((Long)field.getValue()).intValue();
					}
				}
			}
		}

		if (ackDetails.getObjectType().getNumber() == ObjectType.DEVICE.getNumber())
		{
			List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
			if (actions.get(0).getActionType().getNumber() == ActionType.ADD.getNumber())
			{
				List<Field<?>> fields = actions.get(0).getFields();
				for (Field<?> field : fields)
					if (field.getFieldType().getName().equals("Id"))
						deviceId1 = ((Long)field.getValue()).intValue();
				fields = actions.get(1).getFields();
				for (Field<?> field : fields)
					if (field.getFieldType().getName().equals("Id"))
						deviceId2 = ((Long)field.getValue()).intValue();
			}
		}
	}

	@Override
	public ConfigureControl nextConfigureControl()
	{
		if (!configureControlsList.isEmpty())
		{
			ConfigureControl control = configureControlsList.getFirst();
			configureControlsList.removeFirst();
			logger.info("Sending " + control.getObjectType().getName() + " controls...");
			return control;
		}

		switch (currentState)
		{
			case INIT:
			{
				logger.info("Adding devices....");
				currentState = State.ADD_DEVICE;
				List<Action> actions = new ArrayList<Action>();
				actions = new ArrayList<Action>();
				addDeviceActionsToList(actions);
				configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.DEVICE, actions));
				return nextConfigureControl();
			}

			case ADD_DEVICE:
			{
				logger.info("Adding sites....");
				currentState = State.ADD_SITE;
				List<Action> actions = new ArrayList<Action>();
				actions = new ArrayList<Action>();
				addSiteActionsToList(actions);
				configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.SITE, actions));
				return nextConfigureControl();
			}

			case ADD_SITE:
			{
				logger.info("Adding IO points....");
				currentState = State.ADD_IO_POINTS;
				List<Action> actions = new ArrayList<Action>();
				addIoPointActionsToList(actions);
				configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, actions));
				return nextConfigureControl();
			}

			case ADD_IO_POINTS:
			{
				logger.info("Adding Events....");
				currentState = State.ADD_EVENTS;
				List<Action> actions = new ArrayList<Action>();
				addEventActionsToList(actions);
				configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions));
				return nextConfigureControl();
			}

			case ADD_EVENTS:
				break;
		}

		return null;
	}

	private void addDeviceActionsToList(List<Action> actions)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", DEVICE1_ID));
		fields.add(fieldForFieldNameAndValue("Name", "Dave's RTU"));
		fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
		fields.add(fieldForFieldNameAndValue("PortId", COM1));
		fields.add(fieldForFieldNameAndValue("Parameters", SLAVE_ADDR1.toString() + ":1"));
		fields.add(fieldForFieldNameAndValue("RequestTimeout", REQUEST_TIMEOUT));
		fields.add(fieldForFieldNameAndValue("MaxRetries", MAX_RETRIES));
		fields.add(fieldForFieldNameAndValue("RtuBackoffCount", RTU_BACKOFF_COUNT));
		fields.add(fieldForFieldNameAndValue("RtuBackoffTimeout", RTU_BACKOFF_TIMEOUT));
		actions.add(new Action(ActionType.ADD, fields));

		fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", DEVICE2_ID));
		fields.add(fieldForFieldNameAndValue("Name", "Dave's Other RTU"));
		fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
		fields.add(fieldForFieldNameAndValue("PortId", COM1));
		fields.add(fieldForFieldNameAndValue("Parameters", SLAVE_ADDR2.toString() + ":1"));
		fields.add(fieldForFieldNameAndValue("RequestTimeout", REQUEST_TIMEOUT));
		fields.add(fieldForFieldNameAndValue("MaxRetries", MAX_RETRIES));
		fields.add(fieldForFieldNameAndValue("RtuBackoffCount", RTU_BACKOFF_COUNT));
		fields.add(fieldForFieldNameAndValue("RtuBackoffTimeout", RTU_BACKOFF_TIMEOUT));
		actions.add(new Action(ActionType.ADD, fields));
	}

	private void addSiteActionsToList(List<Action> actions)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Name", "Dave's Site"));
		actions.add(new Action(ActionType.ADD, fields));
	}

	private void addEventActionsToList(List<Action> actions)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		fields.add(fieldForFieldNameAndValue("Id", EVENT_ID));
		fields.add(fieldForFieldNameAndValue("Duration", 1000));
		fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
		fields.add(fieldForFieldNameAndValue("Interval", 10));
		fields.add(fieldForFieldNameAndValue("Name", "Daves Not Here"));
		fields.add(fieldForFieldNameAndValue("PollSetId", 300));
		fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
		actions.add(new Action(ActionType.ADD, fields));
	}

	private void addIoPointActionsToList(List<Action> actions)
	{
		actions.add(addIoPointAction(ActionType.ADD, DEVICE1_ID, "MBS;41002;NONE", "U32", 7));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE1_ID, "MBS;41004;NONE", "U16", 5));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE1_ID, "MBS;41005;NONE", "Float_NONE", 8));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;41007;3412", "Float_3412", 8));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;41009;2143", "Float_2143", 8));
		actions.add(addIoPointAction(ActionType.ADD, 0, "MBS;41009;2143", "Orphan_105", 8));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;41012", "UCHAR", 3));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;41013", "DISCRETE-Holding", 1));
		actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;3", "DISCRETE-Coil", 1));
	}

	private Action addIoPointAction(ActionType actionType, Integer externalDeviceId, String sourceAddress, String tag, Integer dataType)
	{
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
		if (actionType.getNumber() != ActionType.DELETE.getNumber())
		{
			fields.add(fieldForFieldNameAndValue("ExternalDeviceId", externalDeviceId));
			fields.add(fieldForFieldNameAndValue("PollSetId", 300));
			fields.add(fieldForFieldNameAndValue("IsReadOnly", 0));
			fields.add(fieldForFieldNameAndValue("SensorClassName", "RTU"));
            fields.add(fieldForFieldNameAndValue("IOPointClass", 2));
			fields.add(fieldForFieldNameAndValue("SiteId", siteId));
			fields.add(fieldForFieldNameAndValue("SourceAddress", sourceAddress));
			fields.add(fieldForFieldNameAndValue("IsSystemPoint", 0));
			fields.add(fieldForFieldNameAndValue("Tag", tag));
			fields.add(fieldForFieldNameAndValue("DataType", dataType));
			fields.add(fieldForFieldNameAndValue("AlarmMask", 0));
		}
		return new Action(actionType, fields);
	}
}
