package zedi.pacbridge.app.monitor;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.app.controls.ControlRequestProcessor;
import zedi.pacbridge.app.controls.ProcessedControlAttachement;
import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.SiteConnectedAttachment;
import zedi.pacbridge.app.net.SiteDisconnectedAttachment;
import zedi.pacbridge.app.zap.ReportNotificationAttachment;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapReportProcessor;
import zedi.pacbridge.zap.reporting.ZapReport;

@Singleton
@Startup
public class SiteMonitor implements Notifiable {

    private SiteStatisticsCache siteStatisticsCache;
    
    public SiteMonitor() {
    }
    
    @Inject
    public SiteMonitor(SiteStatisticsCache siteStatisticsCache, NotificationCenter notificationCenter) {
        this.siteStatisticsCache = siteStatisticsCache;
        notificationCenter.addObserver(this, ZapReportProcessor.PROCESSED_REPORT_FOR_SITE_NOTIFICATION);
        notificationCenter.addObserver(this, Connection.CONNECTION_CONNECTED_NOTIFICATION);
        notificationCenter.addObserver(this, Connection.CONNECTION_CLOSED_NOTIFICATION);
        notificationCenter.addObserver(this, ControlRequestProcessor.CONTROL_PROCESSED_NOTIFICATION);
    }

    @Override
    public void handleNotification(Notification notification) {
        if (ZapReportProcessor.PROCESSED_REPORT_FOR_SITE_NOTIFICATION.equals(notification.getName())) {
            SiteAddress siteAddress = notification.<ReportNotificationAttachment>getAttachment().getSiteAddress();
            ZapReport report = notification.<ReportNotificationAttachment>getAttachment().getReport();
            recordReportForSite(siteAddress, report);
        } else if (Connection.CONNECTION_CONNECTED_NOTIFICATION.equals(notification.getName())) {
            recordConnectionForSite(notification.<SiteConnectedAttachment>getAttachment());
        } else if (Connection.CONNECTION_CLOSED_NOTIFICATION.equals(notification.getName())) {
            recordDisconnectionForSite(notification.<SiteDisconnectedAttachment>getAttachment());
        } else if (ControlRequestProcessor.CONTROL_PROCESSED_NOTIFICATION.equals(notification.getName())) {
            ProcessedControlAttachement attachement = notification.getAttachment();
            recordOutgoingControlForSite(attachement.getSiteAddress(), attachement.getControl());
        }
    }
    
    public void recordConnectionForSite(SiteConnectedAttachment attachment) {
        siteStatisticsCache.recordConnectionForSite(attachment.getSiteAddress());
    }
    
    public void recordDisconnectionForSite(SiteDisconnectedAttachment attachment) {
        siteStatisticsCache.recordDisconnectionForSite(attachment.getSiteAddress());
    }

    public void recordOutgoingControlForSite(SiteAddress siteAddress, Control control) {
        siteStatisticsCache.recordControlForSite(siteAddress, control);
    }

    public void recordReportForSite(SiteAddress address, ZapReport report) {
        siteStatisticsCache.recordReportForSite(address, report);
    }
}
