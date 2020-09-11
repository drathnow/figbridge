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

public class AddSiteConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(AddSiteConfiguration.class);

    private String name;
    private List<Action> actions;

    public AddSiteConfiguration(FieldTypeLibrary fieldTypeLibrary, String name)
    {
        super(fieldTypeLibrary);
        this.name = name;
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
                        logger.info("New Site created with ID: " + ((Long)field.getValue()).intValue());
                }
            }
        }
    }
    
    @Override
    public ConfigureControl nextConfigureControl()
    {
        if (actions == null)
        {
            logger.info("Adding Site....");
            actions = new ArrayList<Action>();
            addSiteActionsToList(actions);
            return new ConfigureControl(eventId.getAndIncrement(), ObjectType.SITE, actions);
        }
        return null;
    }
    
    private void addSiteActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Name", name));
        actions.add(new Action(ActionType.ADD, fields));
    }
}
