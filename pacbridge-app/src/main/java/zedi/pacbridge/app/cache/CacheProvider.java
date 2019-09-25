package zedi.pacbridge.app.cache;

import org.infinispan.Cache;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.monitor.BridgeStatistics;
import zedi.pacbridge.app.monitor.LostConnectionTracker;
import zedi.pacbridge.app.monitor.SiteStatistics;

public interface CacheProvider {    
    public static final String OUTGOING_REQUEST_CACHE_NAME = "OutgoingRequestCache";
    public static final String LOST_CONNECTION_TRACKER_CACHE_NAME = "LostConnectionTrackerCache";
    public static final String SITE_STATISTIC_CACHE_NAME = "SiteStatisticCache";
    public static final String BRIDGE_STATISTICS_CACHE_NAME = "BridgeStatisticsCache";
    public static final String DEVICE_CACHE_NAME = "DeviceCache";
    public static final String INTERESTING_SITES_CACHE_NAME = "InterestingSitesCache";

    public Cache<String, OutgoingRequest> getOutgoingRequestCache();
    public Cache<String, LostConnectionTracker> getLostConnectionTrackerCache();
    public Cache<String, SiteStatistics> getSiteStatisticsCache();
    public Cache<String, BridgeStatistics> getBridgeStatisticsCache();
    public Cache<String, Device> getDeviceCache();
    public Cache<String, Long> getInterestingSitesCache();
}
