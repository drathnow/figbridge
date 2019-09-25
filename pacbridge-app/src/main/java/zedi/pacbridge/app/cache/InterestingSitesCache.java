package zedi.pacbridge.app.cache;

import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.IntegerSystemProperty;

@Stateless
public class InterestingSitesCache {
    private static final Logger logger = LoggerFactory.getLogger(InterestingSitesCache.class.getName());
    private static final String TIMEOUT_MINUTES_PROPERTY_NAME = "interestingSitesCache.timeoutMinutes";
    private static final Integer DEFAULT_TIMEOUT_MINUTES = 10;
    private static final Integer MIN_TIMEOUT_MINUTES = 1;
    private static final Integer MAX_TIMEOUT_MINUTES = 60;
    
    private IntegerSystemProperty timeoutMinutes = new IntegerSystemProperty(TIMEOUT_MINUTES_PROPERTY_NAME, 
                                                                             DEFAULT_TIMEOUT_MINUTES,
                                                                             MIN_TIMEOUT_MINUTES,
                                                                             MAX_TIMEOUT_MINUTES);
    private Cache<String, Long> theCache;
    
    public InterestingSitesCache() {
    }
    
    @Inject
    public InterestingSitesCache(Cache<String, Long> theCache) {
        this.theCache = theCache;
    }

    public void markSiteAsInteresting(String nuid) {
        logger.trace("Marking site as interesting: " + nuid + ", timeout: " + timeoutMinutes + " min.");
        theCache.put(nuid, System.currentTimeMillis(), timeoutMinutes.currentValue(), TimeUnit.MINUTES);
    }
    
    public boolean isSiteIneresting(String nuid) {
        return theCache.containsKey(nuid);
    }
}
