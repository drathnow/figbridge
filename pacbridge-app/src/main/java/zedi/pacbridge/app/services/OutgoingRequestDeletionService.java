package zedi.pacbridge.app.services;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;

public class OutgoingRequestDeletionService {
    private static final Logger logger = LoggerFactory.getLogger(OutgoingRequestDeletionService.class.getName());

    private OutgoingRequestCache outgoingRequestCache;
    private EventHandler eventPublisher;
    
    // Keeps CDI happy
    public OutgoingRequestDeletionService() {
    }
    
    @Inject
    public OutgoingRequestDeletionService(OutgoingRequestCache outgoingRequestCache, EventHandler eventPublisher) {
        this.outgoingRequestCache = outgoingRequestCache;
        this.eventPublisher = eventPublisher;
    }
    
    public boolean deleteOutgoingRequestWithRequestId(String requestId) {
        OutgoingRequest outgoingRequest = outgoingRequestCache.outgoingRequestForRequestId(requestId);
        if (outgoingRequest != null) {
            Long eventId = outgoingRequest.getEventId();
            String nuid = outgoingRequest.getSiteAddress().getAddress();
            ZiosEventResponseEvent event = new ZiosEventResponseEvent(eventId, EventStatus.Cancelled, nuid);
            try {
                eventPublisher.publishEvent(event);
                outgoingRequestCache.deleteOutgoingRequestWithRequestId(requestId);
                return true;
            } catch (Exception e) {
                logger.error("Unable to publish EventResponseEvent", e);
            }
        }
        return false;
    }

}
