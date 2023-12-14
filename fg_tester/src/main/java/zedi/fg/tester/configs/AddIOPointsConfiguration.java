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

public class AddIOPointsConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(AddSiteConfiguration.class);
    
    private List<Action> actions;
    

    public AddIOPointsConfiguration(FieldTypeLibrary fieldTypeLibrary, long siteId, int numberOfIOPoints)
    {
        super(fieldTypeLibrary);
        ConfigureIOPointsEventGnerator generator = new ConfigureIOPointsEventGnerator(fieldTypeLibrary);
        try 
        {
            actions = generator.addIOPointActionsForSiteId(siteId, numberOfIOPoints);
        } 
        catch (Exception e)
        {
            logger.info("Unable to send control", e);
        }
    }
    
    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
        if (ackDetails.getObjectType().getNumber() == ObjectType.IO_POINT.getNumber())
        {
            List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
            if (actions.get(0).getActionType().getNumber() == ActionType.ADD.getNumber())
            {
                List<Field<?>> fields = actions.get(0).getFields();
                for (Field<?> field : fields)
                {
                    if (field.getFieldType().getName().equals("Id"))
                        logger.info("New IO Point created with ID: " + ((Long)field.getValue()).intValue());
                }
            }
        }
    }

    @Override
    public ConfigureControl nextConfigureControl()
    {
        ConfigureControl control = null;
        if (actions.isEmpty() == false)
        {
            int count = 0;
            List<Action> toSendActions = new ArrayList<>();
            while (count++ < 10 && actions.isEmpty() == false)
            {
                toSendActions.add(actions.remove(0));
            }
            control = new ConfigureControl(eventId.getAndIncrement(), ObjectType.IO_POINT, toSendActions);
        }
        return control;
    }

}
