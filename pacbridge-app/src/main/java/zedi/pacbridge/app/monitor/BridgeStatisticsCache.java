package zedi.pacbridge.app.monitor;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;

@Stateless
public class BridgeStatisticsCache {
    private Cache<String, BridgeStatistics> bridgeStatisticsCache;
    
    public BridgeStatisticsCache() {
    }
    
    @Inject
    public BridgeStatisticsCache(Cache<String, BridgeStatistics> bridgeStatisticsCache) {
        this.bridgeStatisticsCache = bridgeStatisticsCache;
    }

    public Collection<BridgeStatistics> allBridgeStatistics() {
        return new ArrayList<BridgeStatistics>(bridgeStatisticsCache.values());
    }

    public void updateBridgeStatistics(BridgeStatistics bridgeStatistics) {
        bridgeStatisticsCache.replace(bridgeStatistics.getName(), bridgeStatistics);
    }

    public void addBridgeStatistics(BridgeStatistics bridgeStatistics) {
        bridgeStatisticsCache.put(bridgeStatistics.getName(), bridgeStatistics);
    }

    public BridgeStatistics bridgeStatisticsForName(String bridgeName) {
        return bridgeStatisticsCache.get(bridgeName);
    }
    
}