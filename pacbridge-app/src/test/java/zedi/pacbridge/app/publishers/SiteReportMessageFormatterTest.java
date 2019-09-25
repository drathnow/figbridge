package zedi.pacbridge.app.publishers;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.clustering.ClusterIndex;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.net.ReasonCode;
import zedi.pacbridge.test.BaseTestCase;


@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteReportMessageFormatter.class, ClusterIndex.class})
public class SiteReportMessageFormatterTest extends BaseTestCase {
    
    private static final Long CLUSTER_INDEX = 654L;
    private static final String NUID = "1234";
    private static final String MESSAGE_ID = "8923490230";
    private static final String SOME_XML = "<Hello>World</Hello>";

    @Test
    public void shouldFormatMessageUninterestingSiteAndHighPriorityReasonCodeWithNormalPriority() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        TextMessage textMessage = mock(TextMessage.class);
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ReasonCode reasonCode = mock(ReasonCode.class);
        
        mockStatic(ClusterIndex.class);
        given(ClusterIndex.newIndex()).willReturn(CLUSTER_INDEX);
        given(siteReport.asXmlString()).willReturn(SOME_XML);
        given(siteReport.getMessageId()).willReturn(MESSAGE_ID);
        given(siteReport.getNuid()).willReturn(NUID);
        given(cache.isSiteIneresting(NUID)).willReturn(false);
        given(siteReport.getReasonCode()).willReturn(reasonCode);
        given(reasonCode.isHighPriority()).willReturn(true);
        
        SiteReportMessageFormatter formatter = new SiteReportMessageFormatter();
        int expectedIndex = (int)ClusterIndex.newIndex();
        
        InOrder order = inOrder(textMessage);
        
        assertSame(textMessage, formatter.formatMessageWithSiteReport(textMessage, siteReport, cache));
        
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(SiteReportMessageFormatter.EVENT_NAME_KEY_NAME, ZiosEventName.SiteReport.getName());
        order.verify(textMessage).setIntProperty(SiteReportMessageFormatter.CLUSTER_INDEX_KEY, expectedIndex);
        order.verify(textMessage).setJMSCorrelationID(MESSAGE_ID);
        order.verify(textMessage).setJMSPriority(EventPublisher.HIGH_PRIORITY);
        order.verify(textMessage).setText(SOME_XML);
    }
    
    @Test
    public void shouldFormatMessageUninterestingSiteAndLowPriorityReasonCodeWithNormalPriority() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        TextMessage textMessage = mock(TextMessage.class);
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ReasonCode reasonCode = mock(ReasonCode.class);
        
        mockStatic(ClusterIndex.class);
        given(ClusterIndex.newIndex()).willReturn(CLUSTER_INDEX);
        given(siteReport.asXmlString()).willReturn(SOME_XML);
        given(siteReport.getMessageId()).willReturn(MESSAGE_ID);
        given(siteReport.getNuid()).willReturn(NUID);
        given(cache.isSiteIneresting(NUID)).willReturn(false);
        given(siteReport.getReasonCode()).willReturn(reasonCode);
        given(reasonCode.isHighPriority()).willReturn(false);
        
        SiteReportMessageFormatter formatter = new SiteReportMessageFormatter();
        int expectedIndex = (int)ClusterIndex.newIndex();
        
        InOrder order = inOrder(textMessage);
        
        assertSame(textMessage, formatter.formatMessageWithSiteReport(textMessage, siteReport, cache));
        
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(SiteReportMessageFormatter.EVENT_NAME_KEY_NAME, ZiosEventName.SiteReport.getName());
        order.verify(textMessage).setIntProperty(SiteReportMessageFormatter.CLUSTER_INDEX_KEY, expectedIndex);
        order.verify(textMessage).setJMSCorrelationID(MESSAGE_ID);
        order.verify(textMessage).setJMSPriority(EventPublisher.NORMAL_PRIORITY);
        order.verify(textMessage).setText(SOME_XML);
    }
    
    @Test
    public void shouldFormatMessageForUninterestingSiteWithNormalPriority() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        TextMessage textMessage = mock(TextMessage.class);
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        
        mockStatic(ClusterIndex.class);
        given(ClusterIndex.newIndex()).willReturn(CLUSTER_INDEX);
        given(siteReport.asXmlString()).willReturn(SOME_XML);
        given(siteReport.getMessageId()).willReturn(MESSAGE_ID);
        given(siteReport.getNuid()).willReturn(NUID);
        given(cache.isSiteIneresting(NUID)).willReturn(false);
        
        SiteReportMessageFormatter formatter = new SiteReportMessageFormatter();
        int expectedIndex = (int)ClusterIndex.newIndex();
        
        InOrder order = inOrder(textMessage);
        
        assertSame(textMessage, formatter.formatMessageWithSiteReport(textMessage, siteReport, cache));
        
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(SiteReportMessageFormatter.EVENT_NAME_KEY_NAME, ZiosEventName.SiteReport.getName());
        order.verify(textMessage).setIntProperty(SiteReportMessageFormatter.CLUSTER_INDEX_KEY, expectedIndex);
        order.verify(textMessage).setJMSCorrelationID(MESSAGE_ID);
        order.verify(textMessage).setJMSPriority(EventPublisher.NORMAL_PRIORITY);
        order.verify(textMessage).setText(SOME_XML);
    }

    @Test
    public void shouldFormatMessageForInterestingSiteWithHighPriority() throws Exception {
        SiteReport siteReport = mock(SiteReport.class);
        TextMessage textMessage = mock(TextMessage.class);
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        
        mockStatic(ClusterIndex.class);
        given(ClusterIndex.newIndex()).willReturn(CLUSTER_INDEX);
        given(siteReport.asXmlString()).willReturn(SOME_XML);
        given(siteReport.getMessageId()).willReturn(MESSAGE_ID);
        given(siteReport.getNuid()).willReturn(NUID);
        given(cache.isSiteIneresting(NUID)).willReturn(true);
        
        SiteReportMessageFormatter formatter = new SiteReportMessageFormatter();
        int expectedIndex = (int)ClusterIndex.newIndex();
        
        InOrder order = inOrder(textMessage);
        
        assertSame(textMessage, formatter.formatMessageWithSiteReport(textMessage, siteReport, cache));
        
        order.verify(textMessage).clearBody();
        order.verify(textMessage).clearProperties();
        order.verify(textMessage).setStringProperty(SiteReportMessageFormatter.EVENT_NAME_KEY_NAME, ZiosEventName.SiteReport.getName());
        order.verify(textMessage).setIntProperty(SiteReportMessageFormatter.CLUSTER_INDEX_KEY, expectedIndex);
        order.verify(textMessage).setJMSCorrelationID(MESSAGE_ID);
        order.verify(textMessage).setJMSPriority(EventPublisher.HIGH_PRIORITY);
        order.verify(textMessage).setText(SOME_XML);
    }

}
