package zedi.pacbridge.app.controls;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.DependencyResolver;


@Listener(sync = false)
public class OutgoingRequestCacheCreateEventListener {
    private static final Logger logger = LoggerFactory.getLogger(OutgoingRequestCacheCreateEventListener.class.getName());

    @CacheEntryCreated
    public void handleCreateEvent(CacheEntryCreatedEvent<String, OutgoingRequest> createEvent) {
        if (createEvent.isPre() == false) {
            OutgoingRequest outgoingRequest = createEvent.getCache().get(createEvent.getKey());
            if (outgoingRequest != null) {
                logger.trace("Queuing outgoing request for " + outgoingRequest.getSiteAddress().toString() );
                OutgoingRequestManager requestManager = DependencyResolver.Implementation.sharedInstance().getImplementationOf(OutgoingRequestManager.class);
                requestManager.queueOutgoingRequest(outgoingRequest);
            }
        }
    }
}