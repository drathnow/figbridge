package zedi.pacbridge.etc;

import zedi.pacbridge.app.messaging.SiteReport;

public interface SiteReportHandler {
    public boolean didHandleSiteReport(SiteReport siteReport);
}
