package zedi.fg.tester.configs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;
import zedi.pacbridge.zap.messages.TimedEventType;

public class SystemIOPointsReportConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{

    private Deque<ConfigureControl> configureControlsList = new ArrayDeque<ConfigureControl>();

    protected SystemIOPointsReportConfiguration(FieldTypeLibrary fieldTypeLibrary)
    {
        super(fieldTypeLibrary);
        List<Action> actions = new ArrayList<>();
        addEventActionsToList(actions);
        configureControlsList.add(new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions));
    }

    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
    }

    @Override
    public ConfigureControl nextConfigureControl()
    {
        if (configureControlsList.isEmpty())
            return null;
        return configureControlsList.removeFirst();
    }


    private void addEventActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", EVENT_ID));
        fields.add(fieldForFieldNameAndValue("Duration", 0));
        fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
        fields.add(fieldForFieldNameAndValue("Interval", 30));
        fields.add(fieldForFieldNameAndValue("Name", "Report System IOs"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 0));
        fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
        actions.add(new Action(ActionType.ADD, fields));

        fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", EVENT_ID+1));
        fields.add(fieldForFieldNameAndValue("Duration", 0));
        fields.add(fieldForFieldNameAndValue("TimedEventType", TimedEventType.REPORT_EVENT_NUMBER));
        fields.add(fieldForFieldNameAndValue("Interval", 30));
        fields.add(fieldForFieldNameAndValue("Name", "Report System IOs"));
        fields.add(fieldForFieldNameAndValue("PollSetId", 255));
        fields.add(fieldForFieldNameAndValue("StartTime", 1476989000));
        actions.add(new Action(ActionType.ADD, fields));
    }

}
