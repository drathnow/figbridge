package zedi.pacbridge.app.zap;

import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

public class ReportNotificationAttachment {
    private SiteAddress siteAddress;
    private ZapReport report;
    
    public ReportNotificationAttachment(SiteAddress siteAddress, ZapReport report) {
        this.siteAddress = siteAddress;
        this.report = report;
    }
    
    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    public ZapReport getReport() {
        return report;
    }
}
