package zedi.pacbridge.app.controls;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.json.JSONObject;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.AckMessage;

public abstract class BaseControlResponseStrategy {
    protected MessageType expectedMessageType;
    protected Integer expectedSequenceNumber;
    protected ControlStatus finalStatus;
    protected String finalStatusMessage;
    protected Long eventId;
    protected Long startTime;
    protected EventHandler eventPublisher;
    
    private InterestingSitesCache interestingSitesCache;
    private SiteAddress siteAddress;
    
    protected BaseControlResponseStrategy(MessageType expectedMessageType, 
                                          Integer expectedSequenceNumber, 
                                          Long eventId, 
                                          SiteAddress siteAddress, 
                                          EventHandler eventPublisher, 
                                          InterestingSitesCache interestingSitesCache) {
        this.expectedMessageType = expectedMessageType;
        this.expectedSequenceNumber = expectedSequenceNumber;
        this.eventId = eventId;
        this.siteAddress = siteAddress;
        this.finalStatus = null;
        this.finalStatusMessage = null;
        this.eventPublisher = eventPublisher;
        this.interestingSitesCache = interestingSitesCache;
        this.startTime = System.currentTimeMillis();
    }
    
    public boolean isFinished() {
        return finalStatus != null;
    }
    
    public void forceFinished(ControlStatus controlStatus, String statusMessage) {
        this.finalStatus = controlStatus;
        this.finalStatusMessage = statusMessage;
    }
    
    public boolean hasTimedOut() {
        return finalStatus == ControlStatus.TIMED_OUT;
    }
    
    public boolean wasSuccessful() {
        return finalStatus == ControlStatus.SUCCESS;
    }
    
    protected SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    protected void markSiteAsInteresting() {
        interestingSitesCache.markSiteAsInteresting(siteAddress.getAddress());
    }
    
    protected boolean isNotProtocolError(AckMessage message) {
        if (message.isProtocolError()) {
            forceFinished(ControlStatus.FAILURE, message.additionalDetails().toString());
            return false;
        }
        return true;
    }
    
    protected boolean isExpectedMessage(AckMessage ackMessage) {
        return ackMessage.getAckedMessageType().equals(expectedMessageType) && ackMessage.sequenceNumber() == expectedSequenceNumber;
    }
    

    protected Event eventResponseWithErrorStatus() {
        JSONObject jsonObject= new JSONObject(mapWithStatus(expectedMessageType.getName()));
        EventStatus eventStatus = eventStatusForControlStatus(finalStatus);
        String extraData = jsonObject.toString();
        return new ZiosEventResponseEvent(eventId, eventStatus, siteAddress.getAddress(), finalStatusMessage, null);
    }

    private Map<String, String> mapWithStatus(String key) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("control", key);
        long diff = System.currentTimeMillis() - startTime;
        map.put("duration", DurationFormatUtils.formatDurationHMS(diff));
        map.put("status", finalStatus.getName());
        if (finalStatusMessage != null)
            map.put("message", finalStatusMessage);
        return map;
    }
    

    private EventStatus eventStatusForControlStatus(ControlStatus controlStatus) {
        if (ControlStatus.FAILURE == controlStatus)
            return EventStatus.Failure;
        if (ControlStatus.CANCELLED == controlStatus)
            return EventStatus.Cancelled;
        if (ControlStatus.SUCCESS == controlStatus)
            return EventStatus.Success;
        if (ControlStatus.FAILURE == controlStatus)
            return EventStatus.Failure;
        return EventStatus.unknownEventWithMessage("For control status : " + controlStatus.toString());
    }
    
}
