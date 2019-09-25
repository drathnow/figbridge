package zedi.pacbridge.app.controls.zap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.controls.BaseControlResponseStrategy;
import zedi.pacbridge.app.controls.ControlResponseStrategy;
import zedi.pacbridge.app.controls.RequestCompletionStrategy;
import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.zios.ConfigureResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureResponseStrategy extends BaseControlResponseStrategy implements ControlResponseStrategy, RequestCompletionStrategy{
    private static final Logger logger = LoggerFactory.getLogger(ConfigureResponseStrategy.class.getName());

    private boolean responseSent;
    private ConfigureResponseAckDetails responseDetails;
    private FieldTypeLibrary fieldTypeLibrary;
    private InterestingSitesCache interestingSitesCache;
    
    public ConfigureResponseStrategy(ConfigureControl configureControl, FieldTypeLibrary fieldTypeLibrary, SiteAddress siteAddress, EventHandler eventPublisher, InterestingSitesCache interestingSitesCache) {
        super(configureControl.messageType(), configureControl.sequenceNumber(), configureControl.getEventId(), siteAddress, eventPublisher, interestingSitesCache);
        this.fieldTypeLibrary = fieldTypeLibrary;
        this.interestingSitesCache = interestingSitesCache;
        this.responseSent = false;
    }
    
    @Override
    public void handleMessage(Message message) {
        if (message.messageType() == ZapMessageType.Acknowledgement) {
            AckMessage ackMessage = (AckMessage)message;
            if (isExpectedMessage(ackMessage) && isNotProtocolError(ackMessage)) {
                responseDetails = ackMessage.additionalDetails();
                finalStatus = ControlStatus.SUCCESS;
                completeProcessing();
            }
        }
    }

    @Override
    public void completeProcessing() {
        if (responseSent == false) {
            responseSent = true;
            if (logger.isDebugEnabled()) {
                logger.debug("Control request processing complete: " 
                        + expectedMessageType.getName() 
                        + ", Status: "
                        + finalStatus.getName());
            }
            
            Event event;
            if (ControlStatus.SUCCESS == finalStatus) {
                List<Action> actions = responseDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
                Long eventId = responseDetails.getEventId();
                ObjectType objectType = responseDetails.getObjectType();
                event = new ConfigureResponseEvent(objectType, eventId, actions, getSiteAddress().getAddress());
            } else
                event = eventResponseWithErrorStatus();
            try {
                eventPublisher.publishEvent(event);
            } catch (Exception e) {
                logger.error("Unable to publish Control Completion event:\n" + event.asXmlString(), e);
            }
        }
    }
    
}