package zedi.pacbridge.app.monitor;

import java.net.SocketAddress;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

public interface RecordingSync {
    public static final String JNDI_NAME = "java:global/RecordingSync";

    public void recordConnectionForSite(SiteAddress siteAddress);
    public void recordOutgoingControlForSite(SiteAddress siteAddress, Control control);
    public void recordConnectionHandlerTime(long timeInMilliseconds);
    public void recordReportForSite(SiteAddress address, ZapReport report);
    public void logException(Exception exception, SocketAddress address, String message);
    public void recordPublishingTime(long timeInMilliseconds);    
}
