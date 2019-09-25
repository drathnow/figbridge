package zedi.pacbridge.zap.messages;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.ResponseSender;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.ZapReportProcessor;
import zedi.pacbridge.zap.reporting.ResponseStatus;
import zedi.pacbridge.zap.reporting.ZapReport;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BundledReportMessageHandler.class, BundledReportAckDetails.class, AckMessage.class})
public class BundledReportMessageHandlerTest extends BaseTestCase {

    private static final Integer REPORT_ID1 = 100;
    private static final Integer REPORT_ID2 = 200;
    private static final Integer SEQ_NUMBER = 42;
    
    @Mock
    private DependencyResolver dependencyResolver;
    @Mock
    private ZapReportProcessor reportProcessor;
    @Mock
    private ResponseSender messageSender;
    @Mock
    private SiteAddress siteAddress;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
        given(dependencyResolver.getImplementationOf(ZapReportProcessor.class)).willReturn(reportProcessor);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }

    @Test
    public void shouldDetectMissingReportAndFlagAsPermanentError() throws Exception {
        ZapReport report1 = mock(ZapReport.class);
        BundledReportAckDetails reportAck = mock(BundledReportAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        
        BundledReportMessage reportMessage = mock(BundledReportMessage.class);
        Set<Integer> reportIds = new TreeSet<>();
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        
        reportIds.add(REPORT_ID1);
        reportIds.add(REPORT_ID2);
        reportMap.put(REPORT_ID1, report1);
        
        given(reportMessage.messageType()).willReturn(ZapMessageType.BundledReport);
        given(reportMessage.reportIds()).willReturn(reportIds);
        given(reportMessage.reportsMap()).willReturn(reportMap);
        given(reportMessage.sequenceNumber()).willReturn(SEQ_NUMBER);
        given(reportProcessor.didProcessReport(siteAddress, report1)).willReturn(true);
        
        whenNew(BundledReportAckDetails.class)
            .withNoArguments()
            .thenReturn(reportAck);
        
        whenNew(AckMessage.class)
            .withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck)
            .thenReturn(ackMessage);
        
        BundledReportMessageHandler handler = new BundledReportMessageHandler();
        
        handler.handleMessageForSiteAddress(reportMessage, siteAddress, messageSender);
        
        verify(reportProcessor).didProcessReport(siteAddress, report1);
        verify(reportAck).addReportStatus(REPORT_ID1, ResponseStatus.OK);
        verify(reportAck).addReportStatus(REPORT_ID2, ResponseStatus.PermanentError);
        verifyNew(AckMessage.class).withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck);
        verify(messageSender).sendResponse(ackMessage);
    }
    
    @Test
    public void shouldHandleReportWithAtLeastOneErrorWhilePublishing() throws Exception {
        ZapReport report1 = mock(ZapReport.class);
        ZapReport report2 = mock(ZapReport.class);
        BundledReportAckDetails reportAck = mock(BundledReportAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        
        BundledReportMessage reportMessage = mock(BundledReportMessage.class);
        Set<Integer> reportIds = new TreeSet<>();
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        
        reportIds.add(REPORT_ID1);
        reportIds.add(REPORT_ID2);
        reportMap.put(REPORT_ID1, report1);
        reportMap.put(REPORT_ID2, report2);
        
        given(reportMessage.messageType()).willReturn(ZapMessageType.BundledReport);
        given(reportMessage.reportIds()).willReturn(reportIds);
        given(reportMessage.reportsMap()).willReturn(reportMap);
        given(reportMessage.sequenceNumber()).willReturn(SEQ_NUMBER);
        given(reportProcessor.didProcessReport(siteAddress, report1)).willReturn(true);
        given(reportProcessor.didProcessReport(siteAddress, report2)).willReturn(false);
        
        whenNew(BundledReportAckDetails.class)
            .withNoArguments()
            .thenReturn(reportAck);
        
        whenNew(AckMessage.class)
            .withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck)
            .thenReturn(ackMessage);
        
        BundledReportMessageHandler handler = new BundledReportMessageHandler();
        
        handler.handleMessageForSiteAddress(reportMessage, siteAddress, messageSender);
        
        verify(reportProcessor).didProcessReport(siteAddress, report1);
        verify(reportAck).addReportStatus(REPORT_ID1, ResponseStatus.OK);
        verify(reportProcessor).didProcessReport(siteAddress, report2);
        verify(reportAck).addReportStatus(REPORT_ID2, ResponseStatus.TransientError);
        verifyNew(AckMessage.class).withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck);
        verify(messageSender).sendResponse(ackMessage);
    }
    
    @Test
    public void shouldHandleReportWithNoProblems() throws Exception {
        ZapReport report1 = mock(ZapReport.class);
        ZapReport report2 = mock(ZapReport.class);
        BundledReportAckDetails reportAck = mock(BundledReportAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        
        BundledReportMessage reportMessage = mock(BundledReportMessage.class);
        Set<Integer> reportIds = new TreeSet<>();
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        
        reportIds.add(REPORT_ID1);
        reportIds.add(REPORT_ID2);
        reportMap.put(REPORT_ID1, report1);
        reportMap.put(REPORT_ID2, report2);
        
        given(reportMessage.messageType()).willReturn(ZapMessageType.BundledReport);
        given(reportMessage.reportIds()).willReturn(reportIds);
        given(reportMessage.reportsMap()).willReturn(reportMap);
        given(reportMessage.sequenceNumber()).willReturn(SEQ_NUMBER);
        given(reportProcessor.didProcessReport(siteAddress, report1)).willReturn(true);
        given(reportProcessor.didProcessReport(siteAddress, report2)).willReturn(true);
        
        whenNew(BundledReportAckDetails.class)
            .withNoArguments()
            .thenReturn(reportAck);
        
        whenNew(AckMessage.class)
            .withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck)
            .thenReturn(ackMessage);
        
        BundledReportMessageHandler handler = new BundledReportMessageHandler();
        
        handler.handleMessageForSiteAddress(reportMessage, siteAddress, messageSender);
        
        verify(reportProcessor).didProcessReport(siteAddress, report1);
        verify(reportAck).addReportStatus(REPORT_ID1, ResponseStatus.OK);
        verify(reportProcessor).didProcessReport(siteAddress, report2);
        verify(reportAck).addReportStatus(REPORT_ID2, ResponseStatus.OK);
        verifyNew(AckMessage.class).withArguments(SEQ_NUMBER, ZapMessageType.BundledReport, reportAck);
        verify(messageSender).sendResponse(ackMessage);
    }
}
