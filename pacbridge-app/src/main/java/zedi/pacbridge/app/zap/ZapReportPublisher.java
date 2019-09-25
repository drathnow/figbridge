package zedi.pacbridge.app.zap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.events.EventResponseEvent;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.messaging.ReportToSiteReportConverter;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.app.util.MessageIDGenerator;
import zedi.pacbridge.etc.SiteReportHandler;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapReportProcessor;
import zedi.pacbridge.zap.reporting.ZapReport;

@Stateless
@EJB(name = ZapReportProcessor.JNDI_NAME, beanInterface = ZapReportProcessor.class)
public class ZapReportPublisher implements ZapReportProcessor {
    
    private ReportToSiteReportConverter converter;
    private SiteReportHandler siteReportHandler;
    private EventHandler eventResponsePublisher;
    private NotificationCenter notificationCenter;
    
    public ZapReportPublisher() {
    }
    
    @Inject
    public ZapReportPublisher(ReportToSiteReportConverter converter, 
                              SiteReportHandler siteReportHandler, 
                              EventHandler eventResponsePublisher, 
                              NotificationCenter notificationCenter) {
        this.converter = converter;
        this.siteReportHandler = siteReportHandler;
        this.eventResponsePublisher = eventResponsePublisher;
        this.notificationCenter = notificationCenter;
    }
    
    @Override
    public boolean didProcessReport(final SiteAddress siteAddress, final ZapReport report) {
        notificationCenter.postNotificationAsync(PROCESSED_REPORT_FOR_SITE_NOTIFICATION, new ReportNotificationAttachment(siteAddress, report));
        converter.init(siteAddress.getAddress(), report);
        SiteReport siteReport = null;
        while ((siteReport = converter.nextReport()) != null) {
            siteReport.setMessageId(MessageIDGenerator.nextMessageId());
            if (siteReportHandler.didHandleSiteReport(siteReport) == false)
                return false;
        }
        
        if (report.getEventId() != 0) {
            EventResponseEvent event = new ZiosEventResponseEvent(report.getEventId(), EventStatus.Success, siteAddress.getAddress());
            eventResponsePublisher.publishEvent(event);
        }
        return true;
    }

}
