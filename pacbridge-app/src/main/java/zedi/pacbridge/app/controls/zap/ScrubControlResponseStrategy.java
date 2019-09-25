package zedi.pacbridge.app.controls.zap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.controls.BaseControlResponseStrategy;
import zedi.pacbridge.app.controls.ControlResponseStrategy;
import zedi.pacbridge.app.controls.RequestCompletionStrategy;
import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.ScrubControl;
import zedi.pacbridge.zap.messages.ScrubControlAckDetails;

public class ScrubControlResponseStrategy extends BaseControlResponseStrategy implements ControlResponseStrategy, RequestCompletionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ScrubControlResponseStrategy.class.getName());
    
    private boolean responseSent;

    public ScrubControlResponseStrategy(ScrubControl forControl, SiteAddress siteAddress, EventHandler eventPublisher, InterestingSitesCache interestingSitesCache) {
        super(forControl.messageType(), forControl.sequenceNumber(), forControl.getEventId(), siteAddress, eventPublisher, interestingSitesCache);
        this.responseSent = false;
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
            Event event = null;
            if (ControlStatus.SUCCESS == finalStatus)
                event = new ZiosEventResponseEvent(eventId, EventStatus.Success, getSiteAddress().getAddress(), ZiosEventName.Scrub);
            else
                event = eventResponseWithErrorStatus();        
            try {
                eventPublisher.publishEvent(event);
            } catch (Exception e) {
                logger.error("Unable to publish Control Completion event:\n" + event.asXmlString(), e);
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (message.messageType() == ZapMessageType.Acknowledgement) {
            AckMessage ackMessage = (AckMessage)message;
            if (isExpectedMessage(ackMessage) && isNotProtocolError(ackMessage)) {
                ScrubControlAckDetails details = ackMessage.additionalDetails();
                ControlStatus finalStatus = details.isSuccessful() ? ControlStatus.SUCCESS : ControlStatus.FAILURE;
                String finalStatusMessage = details.getStatusMessage();
                forceFinished(finalStatus, finalStatusMessage);
                completeProcessing();
            }
        }
    }

}
