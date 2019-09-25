package zedi.pacbridge.app.controls;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.utl.DependencyResolver;


/**
 * The ExpirationManager class managed cache entries expiration by scheduling entries for
 * deletion.  When run in a cluster, each node in the cluster will run an expiration manager
 * and all will monitor the cluster wide cache.  When an entry expires, all ExpirationManagers
 * will try to delete the entry.  Since the operations are transacted, only on instance will
 * perform the removal.
 * 
 * NOTE: This class is required because Infinispan does not currently issue any notifications when
 * an expired entry is removed from the cache. The reasons for this can be found on the internet
 * if you really want to know why. According to Infinispan community formus, this feature is to be
 * added in a future version. So, until Infinispan grows up, we will have to use this class.
 * 
 * @author daver
 *
 */
@Listener(sync = false)
public class ExpirationManager {
    private static final Logger logger = LoggerFactory.getLogger(ExpirationManager.class.getName());
    private Cache<String, OutgoingRequest> theCache;
    private ScheduledThreadPoolExecutor executor;
    private Map<String, ScheduledFuture<?>> futureMap;
    private Long expirationMinutes; 
    
    public ExpirationManager(Cache<String, OutgoingRequest> theCache, Long expirationMinutes) {
        this(theCache, expirationMinutes, new ScheduledThreadPoolExecutor(1), Collections.synchronizedMap(new TreeMap<String, ScheduledFuture<?>>()));
    }

    // For testing
    ExpirationManager(Cache<String, OutgoingRequest> theCache, Long expirationMinutes, ScheduledThreadPoolExecutor executor, Map<String, ScheduledFuture<?>> futureMap) {
        this.theCache = theCache;
        this.executor = executor;
        this.expirationMinutes = expirationMinutes;
        this.futureMap = futureMap;
    }
    
    public void setExpirationMinutes(Long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
    
    @CacheEntryCreated
    public void handleCreateEvent(CacheEntryCreatedEvent<String, OutgoingRequest> createEvent) {
        if (createEvent.isPre() == false) {
            String key = createEvent.getKey();
            OutgoingRequest request = createEvent.getCache().get(key);
            if (request.hasExpired(expirationMinutes)) {
                logger.debug("Outgoint request " + key + " has expired.  It will not be reloaded");
                createEvent.getCache().remove(key);
            } else {
                logger.debug("Scheduling deletion event for " + key + " at " + expirationMinutes + " minutes from now");
                scheduleExpirationForRequestId(key, expirationMinutes, TimeUnit.MINUTES);
            }
        }
    }

    @CacheEntryRemoved
    public void handleRemovedEvent(CacheEntryRemovedEvent<String, OutgoingRequest> removeEvent) {
        if (removeEvent.isPre() == false) {
            String key = removeEvent.getKey();
            logger.debug("OutgoingRequest removed. Id = " + key);
            ScheduledFuture<?> future = futureMap.remove(key);
            if (future != null)
                future.cancel(false);
        }
    }

    private void scheduleExpirationForRequestId(String requestId, long lifeTime, TimeUnit timeUnit) {
        logger.debug("Scheduling outgoing request expiration timer for " + lifeTime + " " + timeUnit.name() + " from now.");
        ScheduledFuture<?> future = executor.schedule(new DeletionEvent(requestId), lifeTime, timeUnit);
        futureMap.put(requestId, future);
    }
    
    class DeletionEvent implements Runnable {
        String requestId;
        
        public DeletionEvent(String requestId) {
            this.requestId = requestId;
        }
        
        @Override
        public void run() {
            OutgoingRequest or = theCache.remove(requestId);
            if (or != null) {
                logger.debug("Outgoing request expired.  Removing from cache: " + or.toString());
                EventHandler eventPublisher = DependencyResolver.Implementation.sharedInstance().getImplementationOf(EventHandler.class);
                ZiosEventResponseEvent event = new ZiosEventResponseEvent(or.getEventId(), EventStatus.Failure, or.getSiteAddress().getAddress(), EventStatus.Failure.getName(), null);
                try {
                    eventPublisher.publishEvent(event);
                } catch (Exception e) {
                    logger.error("Unable to publish event response event", e);
                }
            }
            futureMap.remove(requestId);
        }
    }
}
