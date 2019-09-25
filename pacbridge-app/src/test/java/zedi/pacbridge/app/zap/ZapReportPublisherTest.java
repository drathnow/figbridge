package zedi.pacbridge.app.zap;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.messaging.ReportToSiteReportConverter;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.app.util.MessageIDGenerator;
import zedi.pacbridge.etc.SiteReportHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.reporting.ZapReport;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZapReportPublisher.class, MessageIDGenerator.class, ZiosEventResponseEvent.class})
public class ZapReportPublisherTest extends BaseTestCase {
    private static final String MESSAGE_ID1 = "1";
    private static final String MESSAGE_ID2 = "2";
    private static final String ADDRESS = "FOO";
    private static final Integer NETWORK_NUMBER = 42;
    private static final Long EVENT_ID = 1234L;
    
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private ReportToSiteReportConverter converter;
    @Mock
    private SiteReportHandler siteReportPublisher;
    @Mock
    private EventHandler eventPublisher;
    @Mock
    private NotificationCenter notificationCenter;

    @Test
    public void shouldPublishEventResponseEventIfReportHasEventId() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        ZapReport report = mock(ZapReport.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        mockStatic(MessageIDGenerator.class);

        given(converter.nextReport())
            .willReturn(siteReport)
            .willReturn(null);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(report.getEventId()).willReturn(EVENT_ID);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(MessageIDGenerator.nextMessageId()).willReturn(MESSAGE_ID1);
        given(siteReportPublisher.didHandleSiteReport(siteReport)).willReturn(true);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Success, ADDRESS).thenReturn(event);
        
        ArgumentCaptor<ReportNotificationAttachment> arg = ArgumentCaptor.forClass(ReportNotificationAttachment.class);
        
        ZapReportPublisher publisher = new ZapReportPublisher(converter, siteReportPublisher, eventPublisher, notificationCenter);
        assertTrue(publisher.didProcessReport(siteAddress, report));
 
        verify(siteReportPublisher).didHandleSiteReport(siteReport);
        verify(notificationCenter).postNotificationAsync(eq(ZapReportPublisher.PROCESSED_REPORT_FOR_SITE_NOTIFICATION), arg.capture());
        assertSame(siteAddress, arg.getValue().getSiteAddress());
        assertSame(report, arg.getValue().getReport());
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Success, ADDRESS);
        verify(eventPublisher).publishEvent(event);
        verifyStatic(MessageIDGenerator.class);
        MessageIDGenerator.nextMessageId();
    }
    
    @Test
    public void shouldPublishMultipleSiteReport() throws Exception {
        SiteReport siteReport1 = mock(SiteReport.class);
        SiteReport siteReport2 = mock(SiteReport.class);
        ZapReport report = mock(ZapReport.class);
        mockStatic(MessageIDGenerator.class);

        given(converter.nextReport())
            .willReturn(siteReport1)
            .willReturn(siteReport2)
            .willReturn(null);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(MessageIDGenerator.nextMessageId())
            .willReturn(MESSAGE_ID1)
            .willReturn(MESSAGE_ID2);
        given(siteReportPublisher.didHandleSiteReport(siteReport1)).willReturn(true);
        given(siteReportPublisher.didHandleSiteReport(siteReport2)).willReturn(true);
        
        ArgumentCaptor<ReportNotificationAttachment> arg = ArgumentCaptor.forClass(ReportNotificationAttachment.class);
        
        ZapReportPublisher publisher = new ZapReportPublisher(converter, siteReportPublisher, eventPublisher, notificationCenter);
        assertTrue(publisher.didProcessReport(siteAddress, report));
 
        verify(siteReportPublisher).didHandleSiteReport(siteReport1);
        verify(siteReportPublisher).didHandleSiteReport(siteReport2);
        
        verify(notificationCenter).postNotificationAsync(eq(ZapReportPublisher.PROCESSED_REPORT_FOR_SITE_NOTIFICATION), arg.capture());
        assertSame(siteAddress, arg.getValue().getSiteAddress());
        assertSame(report, arg.getValue().getReport());
        
        verify(eventPublisher, never()).publishEvent(any(ZiosEventResponseEvent.class));
        verifyStatic(MessageIDGenerator.class, times(2));
        MessageIDGenerator.nextMessageId();
    }
    
    @Test
    public void shouldPublishSingleSiteReport() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        ZapReport report = mock(ZapReport.class);
        mockStatic(MessageIDGenerator.class);

        given(converter.nextReport())
            .willReturn(siteReport)
            .willReturn(null);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(MessageIDGenerator.nextMessageId()).willReturn(MESSAGE_ID1);
        given(siteReportPublisher.didHandleSiteReport(siteReport)).willReturn(true);
        
        ArgumentCaptor<ReportNotificationAttachment> arg = ArgumentCaptor.forClass(ReportNotificationAttachment.class);
        
        ZapReportPublisher publisher = new ZapReportPublisher(converter, siteReportPublisher, eventPublisher, notificationCenter);
        assertTrue(publisher.didProcessReport(siteAddress, report));
 
        verify(siteReportPublisher).didHandleSiteReport(siteReport);
        verify(notificationCenter).postNotificationAsync(eq(ZapReportPublisher.PROCESSED_REPORT_FOR_SITE_NOTIFICATION), arg.capture());
        assertSame(siteAddress, arg.getValue().getSiteAddress());
        assertSame(report, arg.getValue().getReport());
        verify(eventPublisher, never()).publishEvent(any(ZiosEventResponseEvent.class));
        verifyStatic(MessageIDGenerator.class);
        MessageIDGenerator.nextMessageId();
    }
}