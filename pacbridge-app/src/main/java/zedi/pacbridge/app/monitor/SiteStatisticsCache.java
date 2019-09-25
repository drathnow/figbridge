package zedi.pacbridge.app.monitor;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

@Stateless
public class SiteStatisticsCache {
    private Cache<String, SiteStatistics> theCache;
    
    public SiteStatisticsCache() {
    }
    
    @Inject
    public SiteStatisticsCache(Cache<String, SiteStatistics> theCache) {
        this.theCache = theCache;
    }

    public SiteStatistics siteStatisticsForSite(String nuid) {
        return trackerForSite(nuid);
    }
    
    public void recordConnectionForSite(SiteAddress siteAddress) {
        SiteStatistics statistics = trackerForSite(siteAddress.getAddress());
        statistics.incrementConnectionCount();
        statistics.setConnected(true);
        updateSiteStatistics(statistics);
    }

    public void recordReportForSite(SiteAddress siteAddress, ZapReport report) {
        SiteStatistics statistics = trackerForSite(siteAddress.getAddress());
        statistics.incrementReportCount(report);
        updateSiteStatistics(statistics);
    }

    public void recordControlForSite(SiteAddress siteAddress, Control control) {
        SiteStatistics statistics = trackerForSite(siteAddress.getAddress());
        statistics.addSentControl(control);
        updateSiteStatistics(statistics);
    }

    public void recordDisconnectionForSite(SiteAddress siteAddress) {
        SiteStatistics statistics = trackerForSite(siteAddress.getAddress());
        statistics.setConnected(false);
    }

    private void updateSiteStatistics(SiteStatistics siteStatistics) {
        theCache.replace(siteStatistics.getSiteAddress(), siteStatistics);
    }

    
    private SiteStatistics trackerForSite(String address) {
        SiteStatistics tracker = theCache.get(address);
        if (tracker == null) {
            tracker = new SiteStatistics(address);
            theCache.put(address, tracker);
        }
        return tracker;
    }
    
}
