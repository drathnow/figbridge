package zedi.pacbridge.app.monitor;

import java.net.SocketAddress;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

public class InMemoryReportingSync implements RecordingSync {
    
    private SiteStatisticsCache siteStatisticsCache;
    
    private ConnectionsPerMinuteTracker connectionsPerMinuteTracker;
    private ConnectionHandlerTimeTracker connectionHandlerTimeTracker;
    private ReportPerMinuteTracker reportPerMinuteTracker;
    private PublishingTimeTracker publishingTimeTracker;
    
    public InMemoryReportingSync(SiteStatisticsCache siteStatisticsCache) {
        this.siteStatisticsCache = siteStatisticsCache;
        this.connectionsPerMinuteTracker = new ConnectionsPerMinuteTracker();
        this.connectionHandlerTimeTracker = new ConnectionHandlerTimeTracker();
        this.reportPerMinuteTracker = new ReportPerMinuteTracker();
        this.publishingTimeTracker = new PublishingTimeTracker();
    }
    
    @Override
    public void recordConnectionForSite(SiteAddress siteAddress) {
        connectionsPerMinuteTracker.incrementConnectCount();
        siteStatisticsCache.recordConnectionForSite(siteAddress);
    }

    @Override
    public void recordOutgoingControlForSite(SiteAddress siteAddress, Control control) {
        siteStatisticsCache.recordControlForSite(siteAddress, control);
    }

    @Override
    public void recordPublishingTime(long timeInMilliseconds) {
        publishingTimeTracker.addPublishingTime(timeInMilliseconds);
    }
    
    @Override
    public void recordConnectionHandlerTime(long timeInMilliseconds) {
        connectionHandlerTimeTracker.addConnectionHandlerTime(timeInMilliseconds);
    }

    @Override
    public void recordReportForSite(SiteAddress siteAddress, ZapReport report) {
        siteStatisticsCache.recordReportForSite(siteAddress, report);
        reportPerMinuteTracker.recordReport(report);
    }

    @Override
    public void logException(Exception exception, SocketAddress address, String message) {
    }
}
