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
import zedi.pacbridge.zap.values.ZapDataType;

public class StoreAndForwardTestConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(StoreAndForwardTestConfiguration.class);

    public static final Integer NO_ALARM_ACTIVE = 0x0;
    public static final Integer LOW_LOW_ALARM_ACTIVE = 0x01;
    public static final Integer LOW_ALARM_ACTIVE = 0x02;
    public static final Integer HIGH_ALARM_ACTIVE = 0x04;
    public static final Integer HIGH_HIGH_ALARM_ACTIVE = 0x08;
    public static final Integer NO_DATA_ACTIVE = 0x10;
    public static final Integer LIMIT_ALARM_MASK = 0x0F;

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
    private Integer siteId1;
    private Integer siteId2;
    private State currentState;

    public StoreAndForwardTestConfiguration(FieldTypeLibrary fieldTypeLibrary)
    {
        super(fieldTypeLibrary);
        this.siteId1 = 0;
        this.siteId2 = 0;
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
                        siteId1 = ((Long)field.getValue()).intValue();
                        logger.info("Got site1 site Id: " + siteId1);
                    }
                }
            }
            
            if (actions.get(1).getActionType().getNumber() == ActionType.ADD.getNumber())
            {
                List<Field<?>> fields = actions.get(1).getFields();
                for (Field<?> field : fields)
                {
                    if (field.getFieldType().getName().equals("Id"))
                    {
                        siteId2 = ((Long)field.getValue()).intValue();
                        logger.info("Got site2 site Id: " + siteId2);
                    }
                }
            }
            
        }
    }

    @Override
    public ConfigureControl nextConfigureControl()
    {
        if (!configureControlsList.isEmpty())
        {
            ConfigureControl control = configureControlsList.removeFirst();
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
                addDeviceActionsToList(actions);
                configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.DEVICE, actions));
                return nextConfigureControl();
            }

            case ADD_DEVICE:
            {
                logger.info("Adding sites....");
                currentState = State.ADD_SITE;
                List<Action> actions = new ArrayList<Action>();
                addSiteActionsToList(actions);
                configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.SITE, actions));
                return nextConfigureControl();
            }

            case ADD_SITE:
            {
                List<Action> actions = new ArrayList<Action>();
                int startAddress = 41001;

                logger.info("Adding IO points....");
                currentState = State.ADD_IO_POINTS;

                while (startAddress < 41115)
                {
                    int count = Math.min(410115 - startAddress, 25);
                    actions = new ArrayList<Action>();
                    startAddress = addIoPointActionsToList(actions, startAddress, count, DEVICE1_ID, siteId1, 300);
                    System.out.println("StartAddress: " + startAddress);
                    configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, actions));
                }

                actions = new ArrayList<Action>();
                addIoPointActionsToList(actions, 42001, 6, DEVICE2_ID, siteId2, 200);
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
        fields.add(fieldForFieldNameAndValue("Name", "Dave's Site 1"));
        actions.add(new Action(ActionType.ADD, fields));

        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's Site 2"));
        actions.add(new Action(ActionType.ADD, fields));
    }

    private void addEventActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", EVENT_ID));
        fields.add(fieldForFieldNameAndValue("Duration", 0));
        fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
        fields.add(fieldForFieldNameAndValue("Interval", 10));
        fields.add(fieldForFieldNameAndValue("Name", "Daves Not Here"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 300));
        fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
        actions.add(new Action(ActionType.ADD, fields));

        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", EVENT_ID+1));
        fields.add(fieldForFieldNameAndValue("Duration", 0));
        fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
        fields.add(fieldForFieldNameAndValue("Interval", 25));
        fields.add(fieldForFieldNameAndValue("Name", "Daves Elsewhere"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 0));
        fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
        actions.add(new Action(ActionType.ADD, fields));

    }

    private int addIoPointActionsToList(List<Action> actions, int startAddress, int count, int deviceId, int siteId, int pollsetId) 
    {
        int nextAddress = startAddress;
        
        for (int i = 0; i < count; i++)
        {
            nextAddress++;
            String sourceAddress = "MBS;" + nextAddress + ";NONE";
            actions.add(addIoPointAction(ActionType.ADD, deviceId, sourceAddress, sourceAddress, ZapDataType.UNSIGNED_INTEGER, false, siteId, pollsetId));
        }
        
        return nextAddress;
    }

    private Action addIoPointAction(ActionType actionType, Integer externalDeviceId, String sourceAddress, String tag, Integer dataType, boolean addAlarms, int siteId, int pollsetId)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        if (actionType.getNumber() != ActionType.DELETE.getNumber())
        {
            fields.add(fieldForFieldNameAndValue("ExternalDeviceId", externalDeviceId));
            fields.add(fieldForFieldNameAndValue("PollSetId", pollsetId));
            fields.add(fieldForFieldNameAndValue("IsReadOnly", 0));
            fields.add(fieldForFieldNameAndValue("SensorClassName", "RTU"));
            fields.add(fieldForFieldNameAndValue("IOPointClass", 2));
            fields.add(fieldForFieldNameAndValue("SiteId", siteId));
            fields.add(fieldForFieldNameAndValue("SourceAddress", sourceAddress));
            fields.add(fieldForFieldNameAndValue("IsSystemPoint", 0));
            fields.add(fieldForFieldNameAndValue("Tag", tag));
            fields.add(fieldForFieldNameAndValue("DataType", dataType));
            
            if (addAlarms)
                addAlarmFieldsToFieldList(fields);
            else
                fields.add(fieldForFieldNameAndValue("AlarmMask", 0));
        }
        return new Action(actionType, fields);
    }

    private void addAlarmFieldsToFieldList(List<Field<?>> fields)
    {
        fields.add(fieldForFieldNameAndValue("AlarmMask", LIMIT_ALARM_MASK));

        fields.add(fieldForFieldNameAndValue("LowLowSet", "500"));
        fields.add(fieldForFieldNameAndValue("LowLowHysteresis", "510"));
        fields.add(fieldForFieldNameAndValue("LowSet", "750"));
        fields.add(fieldForFieldNameAndValue("LowHysteresis", "760"));

        fields.add(fieldForFieldNameAndValue("HighHighSet", "1750"));
        fields.add(fieldForFieldNameAndValue("HighHighHysteresis", "1740"));
        fields.add(fieldForFieldNameAndValue("HighSet", "1500"));
        fields.add(fieldForFieldNameAndValue("HighHysteresis", "1490"));
    }
}
