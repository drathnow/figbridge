package zedi.pacbridge.app.controls.zap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.controls.BaseControlResponseStrategy;
import zedi.pacbridge.app.controls.ControlResponseStrategy;
import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckDetails;
import zedi.pacbridge.zap.messages.AckDetailsType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;
import zedi.pacbridge.zap.messages.WriteIoPointsControlAckDetails;
import zedi.pacbridge.zap.messages.WriteValue;
import zedi.pacbridge.zap.messages.WriteValueAck;


public class WriteIoPointsControlResponseStrategy extends BaseControlResponseStrategy implements ControlResponseStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WriteIoPointsControlResponseStrategy.class.getName());
    public static final String NO_ACKS_ERROR = "No write value responses received";
    public static final String ERROR_MSG = "At least one write operation failed.  Failed ioId(s): [";

    private WriteIoPointsControl control;
    private boolean responseSent;

    public WriteIoPointsControlResponseStrategy(WriteIoPointsControl control, SiteAddress siteAddress, EventHandler eventPublisher, InterestingSitesCache interestingSitesCache) {
        super(control.messageType(), control.sequenceNumber(), control.getEventId(), siteAddress, eventPublisher, interestingSitesCache);
        this.control = control;
        this.responseSent = false;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.messageType() == ZapMessageType.Acknowledgement) {
            AckMessage ackMessage = (AckMessage)message;
            if (isExpectedMessage(ackMessage) && isNotProtocolError(ackMessage)) {
                if (logger.isTraceEnabled()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Recieved ACK for WriteIOPoints control: Status=")
                        .append(finalStatus.getName());
                    if (finalStatus != ControlStatus.SUCCESS)
                        msg.append(", FinalMessage: ").append(finalStatusMessage);
                    logger.trace(msg.toString());
                }
                
                AckDetails ackDetails = ackMessage.additionalDetails();
                if (ackDetails.type() == AckDetailsType.WriteIoPoints) {
                    WriteIoPointsControlAckDetails details = (WriteIoPointsControlAckDetails)ackDetails;
                    Map<Long, WriteValueAck> map = details.ackMap();
                    parseResponseValues(map);
                    if (isFinished())
                        completeProcessing();
                }
            }
        }
    }
    
    private void parseResponseValues(Map<Long, WriteValueAck> map) {
        if (map.size() > 0) {
            List<Long> failedIds = failedIdsFromAckValueMap(map);
            if (failedIds.size() > 0) {
                finalStatus = ControlStatus.FAILURE;
                finalStatusMessage = errorMessageForFailedIds(failedIds);
            } else {
                finalStatus = ControlStatus.SUCCESS;
                finalStatusMessage = null;
            }
        } else {
            finalStatus = ControlStatus.FAILURE;
            finalStatusMessage = NO_ACKS_ERROR;
        }
    }

    private String errorMessageForFailedIds(List<Long> failedIds) {
        StringBuilder stringBuilder = new StringBuilder(ERROR_MSG);
        for (Iterator<Long> iter = failedIds.iterator(); iter.hasNext();) {
            stringBuilder.append(iter.next()).append(',');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    private List<Long> failedIdsFromAckValueMap(Map<Long, WriteValueAck> map) {
        List<Long> failedIds = new ArrayList<>();
        for (Iterator<WriteValue> iter = control.getWriteValues().iterator(); iter.hasNext();) {
            WriteValue nextValue = iter.next();
            WriteValueAck ack = map.get(nextValue.getIoId());
            if (ack != null && ack.isSuccess() == false)
                failedIds.add(nextValue.getIoId());
        }
        return failedIds;
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
            if (ControlStatus.SUCCESS == finalStatus) {
                markSiteAsInteresting();
                event = new ZiosEventResponseEvent(eventId, EventStatus.Success, getSiteAddress().getAddress());
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
