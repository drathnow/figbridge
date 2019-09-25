package zedi.pacbridge.app.controls;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.IntegerSystemProperty;

@Stateless
public class ControlRequestProgressListener implements RequestProgressListener {
    private static Logger logger = LoggerFactory.getLogger(ControlRequestProgressListener.class.getName());
    public static final String MAX_RETRIES_PROPERTY_NAME = "controls.maxRetries";
    public static final Integer DEFAULT_MAX_RETRIES = 3;
    public static final Integer MIN_RETRIES = 1;
    public static final String MAX_RETRIES_ERROR = "Maximum number of retries exceeded";
    
    private static final IntegerSystemProperty maxRetriesProperty = new IntegerSystemProperty(MAX_RETRIES_PROPERTY_NAME, DEFAULT_MAX_RETRIES, MIN_RETRIES);
    
    private OutgoingRequestCacheUpdateDelegate cacheDelegate;
    private EventHandler eventPublisher;
    private Integer maxRetries;

    @Inject
    public ControlRequestProgressListener(EventHandler eventPublisher, OutgoingRequestCacheUpdateDelegate cacheDelegate) {
        this.eventPublisher = eventPublisher;
        this.cacheDelegate = cacheDelegate;
        this.maxRetries = maxRetriesProperty.currentValue() + 1;
    }

    public ControlRequestProgressListener() {
    }
    
    @Override
    public void requestProcessingStarted(OutgoingRequest outgoingRequest) {
        outgoingRequest.setStatus(ControlStatus.RUNNING);
        cacheDelegate.updateOutgoingRequest(outgoingRequest);
        ZiosEventResponseEvent event = new ZiosEventResponseEvent(outgoingRequest.getEventId(), EventStatus.Running, outgoingRequest.getSiteAddress().getAddress());
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            logger.error("Unable to publish ReponseEvent", e);
        }
    }
    
    public void requestProcessingCompleted(OutgoingRequest outgoingRequest, RequestCompletionStrategy completionStrategy) {
        if (completionStrategy.hasTimedOut()) {
            if (outgoingRequest.getSendAttempts() <= maxRetries) {
                outgoingRequest.setStatus(ControlStatus.PENDING);
                cacheDelegate.updateOutgoingRequest(outgoingRequest);
                publishResponseEvent(new ZiosEventResponseEvent(outgoingRequest.getEventId(), EventStatus.Pending, outgoingRequest.getSiteAddress().getAddress()));
            } else {
                cacheDelegate.deleteOutgoingRequestWithRequestId(outgoingRequest.getRequestId());
                publishResponseEvent(new ZiosEventResponseEvent(outgoingRequest.getEventId(), EventStatus.Failure, outgoingRequest.getSiteAddress().getAddress(), MAX_RETRIES_ERROR, null));
            }
        } else {
            cacheDelegate.deleteOutgoingRequestWithRequestId(outgoingRequest.getRequestId());
            completionStrategy.completeProcessing();
        }
    }

    @Override
    public void requestProcessingAborted(OutgoingRequest outgoingRequest, ControlStatus status, String message, JSONArray extraData) {
        cacheDelegate.deleteOutgoingRequestWithRequestId(outgoingRequest.getRequestId());
        ZiosEventResponseEvent responseEvent = new ZiosEventResponseEvent(outgoingRequest.getEventId(), EventStatus.Failure, outgoingRequest.getSiteAddress().getAddress(), message, null);
        publishResponseEvent(responseEvent);
    }
    
    private void publishResponseEvent(ZiosEventResponseEvent responseEvent) {
        try {
            eventPublisher.publishEvent(responseEvent);
        } catch (Exception e) {
            logger.error("Unable to publish ReponseEvent", e);
        }
    }
}
