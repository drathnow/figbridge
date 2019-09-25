package zedi.pacbridge.app.publishers;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Test;
import org.mockito.InOrder;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.ReflectionHelper;

public class SiteReportPublisherTest extends BaseTestCase {

    private static final Integer PRIORITY = 10;

    @Test
    public void shouldPublishMessage() throws Exception {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Topic topic = mock(Topic.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageProducer producer = mock(MessageProducer.class);
        TextMessage textMessage = mock(TextMessage.class);
        SiteReportMessageFormatter formatter = mock(SiteReportMessageFormatter.class);
        SiteReport siteReport = mock(SiteReport.class);
        NotificationCenter center = mock(NotificationCenter.class);
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        
        given(connectionFactory.createConnection()).willReturn(connection);
        given(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).willReturn(session);
        given(session.createTextMessage()).willReturn(textMessage);
        given(textMessage.getJMSPriority()).willReturn(PRIORITY);
        given(session.createProducer(topic)).willReturn(producer);
        given(formatter.formatMessageWithSiteReport(textMessage, siteReport, cache)).willReturn(textMessage);
 
        InOrder order = inOrder(connectionFactory, connection, session, formatter, center, producer);
        
        SiteReportPublisher publisher = new SiteReportPublisher(center, cache, formatter);
        ReflectionHelper.setObjectsFieldWithValue(publisher, "connectionFactory", connectionFactory);
        ReflectionHelper.setObjectsFieldWithValue(publisher, "topic", topic);
        assertTrue(publisher.didHandleSiteReport(siteReport));
        
        order.verify(connectionFactory).createConnection();
        order.verify(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        order.verify(session).createProducer(topic);
        order.verify(formatter).formatMessageWithSiteReport(textMessage, siteReport, cache);
        order.verify(producer).send(textMessage, DeliveryMode.PERSISTENT, textMessage.getJMSPriority(), producer.getTimeToLive());
        order.verify(center).postNotificationAsync(eq(SiteReportPublisher.RAWDATA_REPORT_PUBLISHED_NOTIFICATION), any(Long.class));
        order.verify(producer).close();
        order.verify(session).close();
        order.verify(connection).close();
    }
}
