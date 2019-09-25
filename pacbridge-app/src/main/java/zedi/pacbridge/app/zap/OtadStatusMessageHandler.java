package zedi.pacbridge.app.zap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.EventData;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.OtadStatus;
import zedi.pacbridge.zap.messages.OtadStatusMessage;
import zedi.pacbridge.zap.messages.ZapOtadStatusMessageHandler;

@Stateless
@EJB(name = ZapOtadStatusMessageHandler.JNDI_NAME, beanInterface = ZapOtadStatusMessageHandler.class)
public class OtadStatusMessageHandler implements ZapOtadStatusMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUpdateHandler.class.getName());
    
    public static final String STEP_NAME = "Step";
    public static final String PERCENT_NAME = "Percent";
    public static final String MESSAGE = "Message";
    public static final String VERSION = "Version";
    
    private EventHandler eventPublisher;
    
    @Inject
    public OtadStatusMessageHandler(EventHandler eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public boolean didProcessStatusUpdateMessage(SiteAddress siteAddress, OtadStatusMessage statusMessage) {
        logger.debug("Recieved OTAD Status Update Message: " + statusMessage.toString());
        
        String message = null;
        EventData eventData = new EventData();
        EventStatus eventStatus = EventStatus.Processing;
        eventData.addProperty(STEP_NAME, statusMessage.getOtadStatusType().getName());
        
        switch (statusMessage.getOtadStatusType().getNumber()) {
            case OtadStatus.OTAD_DOWNLOADING_NUMBER :
                message = statusMessage.getOtadStatusType().getName() + " " + statusMessage.getOptionalData() + "%";
                if (statusMessage.getOptionalData() != null)
                        eventData.addProperty(PERCENT_NAME, statusMessage.getOptionalData());
                break;
                
            case OtadStatus.OTAD_COMPLETE_NUMBER :
                eventStatus = EventStatus.Success;
                message = statusMessage.getOtadStatusType().getName();
                if (statusMessage.getOptionalData() != null) 
                    eventData.addProperty(VERSION, statusMessage.getOptionalData());
                break;
                
            case OtadStatus.OTAD_FAILED_NUMBER :
                eventStatus = EventStatus.Failure;
                message = statusMessage.getOtadStatusType().getName();
                if (statusMessage.getOptionalData() != null) {
                    message += " - " + statusMessage.getOptionalData();
                    eventData.addProperty(MESSAGE, statusMessage.getOptionalData());
                }
                break;
                
            case OtadStatus.OTAD_INSTALLING_NUMBER :
            case OtadStatus.OTAD_UNPACKING_NUMBER :
                message = statusMessage.getOtadStatusType().getName();
                break;
        }
        
        ZiosEventResponseEvent event = new ZiosEventResponseEvent(statusMessage.getEventId(), 
                                                                  eventStatus, 
                                                                  siteAddress.getAddress(), 
                                                                  ZiosEventName.OtadRequest, 
                                                                  message,
                                                                  eventData);
        try {
            eventPublisher.publishEvent(event);
            return true;
        } catch (Exception e) {
            logger.error("Unable to publish OtadStatusUpdate event", e);
            return false;
        }
    }
}
