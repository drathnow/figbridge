package zedi.fg.tester.configs;

import java.util.ArrayList;
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

public class AddEventConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(AddEventConfiguration.class);

    private String name;
    private Integer startTime = 0;
    private Integer interval = 0;
    private Integer duration = 0;
    private Integer pollsetId = 0;
    private TimedEventType eventType;
    List<Action> actions;

    public AddEventConfiguration(FieldTypeLibrary fieldTypeLibrary, String name, TimedEventType eventType, Integer startTime, Integer interval, Integer duration, Integer pollsetId)
    {
        super(fieldTypeLibrary);
        this.name = name;
        this.startTime = startTime;
        this.interval = interval;
        this.duration = duration;
        this.pollsetId = pollsetId;
        this.eventType = eventType;
    }

    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
        if (ackDetails.getObjectType().getNumber() == ObjectType.EVENT.getNumber())
        {
            List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
            if (actions.get(0).getActionType().getNumber() == ActionType.ADD.getNumber())
            {
                List<Field<?>> fields = actions.get(0).getFields();
                for (Field<?> field : fields)
                {
                    if (field.getFieldType().getName().equals("Id"))
                        logger.info("New Event created with ID: " + ((Long)field.getValue()).intValue());
                }
            }
        }
    }

    @Override
    public ConfigureControl nextConfigureControl()
    {
        if (actions == null)
        {
            logger.info("Adding Events....");
            actions = new ArrayList<Action>();
            addEventActionsToList(actions);
            return new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions);
        }
        return null;
    }

    private void addEventActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", 0));
        fields.add(fieldForFieldNameAndValue("Duration", duration));
        fields.add(fieldForFieldNameAndValue("TimedEventType", eventType.getNumber()));
        fields.add(fieldForFieldNameAndValue("Interval", interval));
        fields.add(fieldForFieldNameAndValue("Name", name));
        fields.add(fieldForFieldNameAndValue("PollSetId", pollsetId));
        fields.add(fieldForFieldNameAndValue("StartTime", startTime));
        actions.add(new Action(ActionType.ADD, fields));
    }

}
