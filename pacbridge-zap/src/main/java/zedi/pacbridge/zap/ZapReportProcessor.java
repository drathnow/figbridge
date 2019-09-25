package zedi.pacbridge.zap;

import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

public interface ZapReportProcessor {
    public static final String JNDI_NAME = "java:global/ZapReportProcessor";
    public static final String PROCESSED_REPORT_FOR_SITE_NOTIFICATION = "ZapReportProcessor.processedReportForSite";
    
    public boolean didProcessReport(SiteAddress siteAddress, ZapReport report);
}
