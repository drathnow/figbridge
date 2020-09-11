package zedi.fg.tester.configs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
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

public class A1000TestConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(A1000TestConfiguration.class);

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
    public static final Integer SLAVE_ADDR1 = 31;
    public static final Integer SLAVE_ADDR2 = 31;
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
    private IOActionLoad actionLoad;

    public A1000TestConfiguration(FieldTypeLibrary fieldTypeLibrary)
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
                if (actionLoad == null)
                    actionLoad = new IOActionLoad(10);
                
                logger.info("Adding IO points....");
                currentState = State.ADD_IO_POINTS;
                List<Action> actions = new ArrayList<Action>();
                while (actionLoad.nextLoadOfActions(actions) > 0)
                {
                    configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, actions));
                    actions = new ArrayList<Action>();
                }
                return nextConfigureControl();
            }

            case ADD_IO_POINTS:
            case ADD_EVENTS:
                break;
        }

        return null;
    }
    
    private void addDeviceActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
//        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
//        fields.add(fieldForFieldNameAndValue("Id", DEVICE1_ID));
//        fields.add(fieldForFieldNameAndValue("Name", "Dave's RTU on COM1"));
//        fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
//        fields.add(fieldForFieldNameAndValue("PortId", COM2));
//        fields.add(fieldForFieldNameAndValue("Parameters", SLAVE_ADDR1.toString() + ":1"));
//        fields.add(fieldForFieldNameAndValue("RequestTimeout", REQUEST_TIMEOUT));
//        fields.add(fieldForFieldNameAndValue("MaxRetries", MAX_RETRIES));
//        fields.add(fieldForFieldNameAndValue("RtuBackoffCount", RTU_BACKOFF_COUNT));
//        fields.add(fieldForFieldNameAndValue("RtuBackoffTimeout", RTU_BACKOFF_TIMEOUT));
//        actions.add(new Action(ActionType.ADD, fields));
//
        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", DEVICE2_ID));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's RTU on COM1"));
        fields.add(fieldForFieldNameAndValue("ProtocolId", PROTOCOL_ID_MODBUS));
        fields.add(fieldForFieldNameAndValue("PortId", COM2));
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
        
        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's Other Site"));
        actions.add(new Action(ActionType.ADD, fields));

    }

    private Action addIoPointAction(ActionType actionType, Integer externalDeviceId, String sourceAddress, String tag, Integer dataType, boolean addAlarms, Integer siteId, Integer pollsetId)
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
    
    
    class IOActionLoad
    {
        private List<Action> actions = new ArrayList<>();
        private Iterator<Action> actionIterator;
        private int bulkCount = 0;
        
        public IOActionLoad(int bulkCount)
        {
            this.bulkCount = bulkCount;
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40036;", "Drive Monitor - Frequency Reference", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40037;", "Drive Monitor - Output Frequency", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40038;", "Drive Monitor - Output Voltage Reference", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40039;", "Drive Monitor - Output Current", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40040;", "Drive Monitor - Output Power", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40041;", "Drive Monitor - Torque Reference", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40050;", "Drive Monitor - DC Bus Voltage", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40051;", "Drive Monitor - Torque Reference U1-09", ZapDataType.UNSIGNED_INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40033/0;", "Drive Status - During Run", ZapDataType.INTEGER, false, siteId1, 300));
            
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40033/1;", "Drive Status - During Reverse", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40033/3;", "Drive Status - Fault", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/0;", "Fault Contents - Overcurrent (oC) Ground fault (GF)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/1;", "Fault Contents - Drive Overheat Warning (ov)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/2;", "Fault Contents - Drive Overload (oL2)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/3;", "Fault Contents - Overheat 1 (oH1) Drive Overheat Warning (oH2)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/4;", "Fault Contents - Dynamic Braking Transistor Fault (rr) Braking Resistor Overheat (rH)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/6;", "Fault Contents - PID Feedback Loss (FbL - FbH)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/7;", "Fault Contents - EF to EF8 External Fault", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/8;", "Fault Contents - CPF Hardware Fault (includes oFx)", ZapDataType.INTEGER, false, siteId1, 300));

            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/9;", "Fault Contents - Motor Overload (oL1) (oL3-oL4)  (UL3-UL4)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/10;", "Fault Contents - PG Disconnected (PGo) (PGoH) (oS) (dEv)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/11;", "Fault Contents - Main Circuit Undervoltage (Uv)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/12;", "Fault Contents - DC Bus Undervoltage (Uv1) (Uv2) (Uv3)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/13;", "Fault Contents - Output Phase Loss (LF) Input Phase Loss (PF)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/14;", "Fault Contents - MEMOBUS-Modbus Communication Error (CE)(bUS)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40034/15;", "Fault Contents - External Digital Operator Connection Fault (oPr)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/0;", "Fault Contents - Output Short Circuit or IGBT Fault (SC)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/1;", "Fault Contents - Ground Fault (GF)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/2;", "Fault Contents - Input Phase Loss (PF)", ZapDataType.INTEGER, false, siteId1, 300));
            
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/3;", "Fault Contents - Output Phase Loss (LF)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/4;", "Fault Contents - Braking Resistor Overheat (rH)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40042/6;", "Fault Contents - Motor Overheat 2 (PTC input) (oH4)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/2;", "Alarm Contents - Forward-Reverse Run Command Input Error (EF)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/3;", "Alarm Contents - Drive Baseblock (bb)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/4;", "Alarm Contents - Overtorque Detection 1 (oL3)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/5;", "Alarm Contents - Heatsink Overheat (oH)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/6;", "Alarm Contents - Drive Overheat Warning (ov)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/7;", "Alarm Contents - Undervoltage (Uv)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/8;", "Alarm Contents - Internal Fan Fault (FAn)", ZapDataType.INTEGER, false, siteId1, 300));
            
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/9;", "Alarm Contents - MEMOBUS-Modbus Communication Error (CE)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/10;", "Alarm Contents - Option Communication Error (bUS)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/11;", "Alarm Contents - Undertorque Detection 1of2 (UL3-UL4)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/12;", "Alarm Contents - Motor Overheat (oH3)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/13;", "Alarm Contents - PID Feedback Loss (FbL FbH)", ZapDataType.INTEGER, false, siteId1, 300));
            actions.add(addIoPointAction(ActionType.ADD, DEVICE2_ID, "MBS;40043/14;", "Alarm Contents - Serial Communication Transmission Error (CALL)", ZapDataType.INTEGER, false, siteId1, 300));
            
            actionIterator = actions.iterator();
        }
        
        public int nextLoadOfActions(List<Action> actions)
        {
            int count = 0;
            if (actionIterator.hasNext() && count < bulkCount)
            {
                actions.add(actionIterator.next());
                count++;
            }
            
            return count;
        }
    }
}
