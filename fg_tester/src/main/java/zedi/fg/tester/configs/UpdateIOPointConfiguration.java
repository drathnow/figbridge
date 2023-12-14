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
import zedi.pacbridge.zap.values.ZapDataType;

public class UpdateIOPointConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{

    private static final Logger logger = Logger.getLogger(UpdateIOPointConfiguration.class);
    
    private List<Action> actions = new ArrayList<>();

    public UpdateIOPointConfiguration(FieldTypeLibrary fieldTypeLibrary, long siteId, long ioId, String tag, ZapDataType dataType)
    {
        super(fieldTypeLibrary);
        ConfigureIOPointsEventGnerator generator = new ConfigureIOPointsEventGnerator(fieldTypeLibrary);
        try 
        {
            actions.add(generator.updateOPointActionForIOPoint(siteId, ioId, tag, dataType));
        } 
        catch (Exception e)
        {
            logger.info("Unable to build IO Point Update action", e);
        }
    }

    
    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
        if (ackDetails.getObjectType().getNumber() == ObjectType.IO_POINT.getNumber())
        {
            List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
            if (actions.get(0).getActionType().getNumber() == ActionType.UPDATE.getNumber())
            {
                List<Field<?>> fields = actions.get(0).getFields();
                for (Field<?> field : fields)
                {
                    if (field.getFieldType().getName().equals("Id"))
                        logger.info("IO Point updated ID: " + ((Long)field.getValue()).intValue());
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
