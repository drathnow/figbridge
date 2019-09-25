package zedi.pacbridge.app.monitor;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.SiteAddress;

@Stateless
public class LostConnectionTrackerCache {
    private static final Logger logger = LoggerFactory.getLogger(LostConnectionTrackerCache.class.getName());
    
    private Cache<String, LostConnectionTracker> theCache;
    
    public LostConnectionTrackerCache() {
    }
    
    @Inject
    public LostConnectionTrackerCache(Cache<String, LostConnectionTracker> theCache) {
        this.theCache = theCache;
    }

    /**
     * Updates an entry in the cache. It is possible that the update will fail if the 
     * entry has been deleted from the cache.
     * 
     * @param LostConncectionTracke
     * @return true if the entry exists and was updated, false if it was not found in the cache.
     */
    public boolean updateLostConncectionTracker(LostConnectionTracker tracker) {
        logger.trace("Updating LostConnectionTracker request for " + tracker.siteAddress().toString());
        return theCache.replace(tracker.siteAddress(), tracker) != null;
    }
    
    public void storeLostConncectionTracker(LostConnectionTracker tracker) {
        logger.trace("Adding LostConnectionTracker request for " + tracker.siteAddress().toString());
        theCache.put(tracker.siteAddress(), tracker);
    }
    
    public boolean deleteLostConncectionTracker(SiteAddress siteAddress) {
        logger.trace("Deleting LostConnectionTracker request for " + siteAddress.toString());
        return theCache.remove(siteAddress.getAddress()) != null;
    }
}
