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

public class DeleteEventConfiguration extends BaseTestConfiguration implements ConfigurationSetup
{
    private static final Logger logger = Logger.getLogger(DeleteEventConfiguration.class);

    private Integer id = 0;
    private List<Action> actions;
    
    protected DeleteEventConfiguration(FieldTypeLibrary fieldTypeLibrary, Integer id)
    {
        super(fieldTypeLibrary);
        this.id = id;
    }

    @Override
    public void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails)
    {
        if (ackDetails.getObjectType().getNumber() == ObjectType.EVENT.getNumber())
        {
            List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
            if (actions.get(0).getActionType().getNumber() == ActionType.DELETE.getNumber())
            {
                List<Field<?>> fields = actions.get(0).getFields();
                for (Field<?> field : fields)
                {
                    if (field.getFieldType().getName().equals("ErrCode"))
                        logger.info("Delete event status: " + ((Long)field.getValue()).intValue());
                }
            }
        }
    }

    @Override
    public ConfigureControl nextConfigureControl()
    {
        if (actions == null)
        {
            logger.info("Deleting Event....");
            actions = new ArrayList<Action>();
            addEventActionsToList(actions);
            return new ConfigureControl(eventId.getAndIncrement(), ObjectType.EVENT, actions);
        }
        
        return null;    }
    
    private void addEventActionsToList(List<Action> actions)
    {
        List<Field<?>> fields = new ArrayList<Field<?>>();
        fields.add(fieldForFieldNameAndValue("CorrelationId", correlationId.getAndIncrement()));
        fields.add(fieldForFieldNameAndValue("Id", id));
        actions.add(new Action(ActionType.DELETE, fields));
    }

}
