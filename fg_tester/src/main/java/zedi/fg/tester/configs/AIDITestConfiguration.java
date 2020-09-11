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

public class AIDITestConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(AIDITestConfiguration.class);

    public static final int AI_CORR_ID = 10005;
    public static final int DI_CORR_ID = 10006;
    public static final int RTD_CORR_ID = 10007;
    public static final Integer DI_POLLSET_ID = 500;
    public static final Integer AI_POLLSET_ID = 501;
    public static final Integer DO_POLLSET_ID = 502;
    public static final Integer RTD_POLLSET_ID = 503;

    enum State
    {
        INIT, ADD_SITE, ADD_IO_POINTS, ADD_EVENTS
    }

    private Deque<ConfigureControl> configureControlsList = new ArrayDeque<ConfigureControl>();
    private Integer aiSiteId = 0;
    private Integer diSiteId = 0;
    private Integer rtdSiteId = 0;
    private State currentState = State.INIT;

    public AIDITestConfiguration(FieldTypeLibrary fieldTypeLibrary)
    {
        super(fieldTypeLibrary);
    }

    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
        if (ackDetails.getObjectType().getNumber() == ObjectType.SITE.getNumber())
        {
            List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
            for (Action action: actions) 
                if (action.getActionType().getNumber() == ActionType.ADD.getNumber()) 
                    extractSiteIdFromFieldList(action.getFields());
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
                logger.info("Adding Sites....");
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
                currentState = State.ADD_EVENTS;
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

    private void addSiteActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", new Long(AI_CORR_ID)));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's AI Site"));
        actions.add(new Action(ActionType.ADD, fields));

        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", new Long(DI_CORR_ID)));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's DI Site"));
        actions.add(new Action(ActionType.ADD, fields));

        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", new Long(RTD_CORR_ID)));
        fields.add(fieldForFieldNameAndValue("Name", "Dave's RTD Site"));
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
        for (int i = 1; i < 9; i++)
        {
            actions.add(addIoPointAction(ActionType.ADD, "AI." + i, "AI." + i, AI_POLLSET_ID, aiSiteId, true));
            if (i % 2 == 1) 
            {
                actions.add(addIoPointAction(ActionType.ADD, "DI." + i, "DI." + i, DI_POLLSET_ID, diSiteId, true));
                actions.add(addIoPointAction(ActionType.ADD, "DI." + (i+8), "DI." + (i+8), DI_POLLSET_ID, diSiteId, true));
            }
            else
            {
                actions.add(addIoPointAction(ActionType.ADD, "DI." + i + "*", "DI." + i + "*", DI_POLLSET_ID, diSiteId, true));
                actions.add(addIoPointAction(ActionType.ADD, "DI." + (i+8) + "*", "DI." + (i+8) + "*", DI_POLLSET_ID, diSiteId, true));
            }
            actions.add(addIoPointAction(ActionType.ADD, "DO." + (i+8), "DO." + (i+8), DO_POLLSET_ID, diSiteId, false));
        }

        actions.add(addIoPointAction(ActionType.ADD, "RTD.1", "RTD.1", RTD_POLLSET_ID, rtdSiteId, true));
        actions.add(addIoPointAction(ActionType.ADD, "RTD.2", "RTD.2", RTD_POLLSET_ID, rtdSiteId, true));
    }

    private Action addIoPointAction(ActionType actionType, String sourceAddress, String tag, Integer pollsetId, Integer siteId, boolean readonly)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("ExternalDeviceId", 0));
        fields.add(fieldForFieldNameAndValue("PollSetId", pollsetId));
        fields.add(fieldForFieldNameAndValue("IsReadOnly", readonly ? 1 : 0));
        fields.add(fieldForFieldNameAndValue("SensorClassName", "INT"));
        fields.add(fieldForFieldNameAndValue("IOPointClass", 3));        
        fields.add(fieldForFieldNameAndValue("SiteId", siteId));
        fields.add(fieldForFieldNameAndValue("SourceAddress", sourceAddress));
        fields.add(fieldForFieldNameAndValue("IsSystemPoint", 0));
        fields.add(fieldForFieldNameAndValue("Tag", tag));
        fields.add(fieldForFieldNameAndValue("DataType", 8));
        fields.add(fieldForFieldNameAndValue("AlarmMask", 0));
        return new Action(actionType, fields);
    }

    private void extractSiteIdFromFieldList(List<Field<?>> fieldList)
    {
        Long correlationId = 0L;
        Integer siteId = 0;
        for (Field<?> field : fieldList)
        {
            if (field.getFieldType().getName().equals("Id"))
                siteId = ((Long)field.getValue()).intValue();
            if (field.getFieldType().getName().equals("CorrelationId"))
                correlationId = (Long)field.getValue();
        }
        
        switch (correlationId.intValue()) 
        {
            case AI_CORR_ID :
                aiSiteId = siteId;
                break;
                
            case DI_CORR_ID :
                diSiteId = siteId;
                break;

            case RTD_CORR_ID :
                rtdSiteId = siteId;
                break;
        }
    }
}