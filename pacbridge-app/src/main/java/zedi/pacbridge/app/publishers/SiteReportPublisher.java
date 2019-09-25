package zedi.pacbridge.app.publishers;

import java.util.Enumeration;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.log4j.Logger;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.etc.SiteReportHandler;
import zedi.pacbridge.utl.NotificationCenter;

@Stateless
public class SiteReportPublisher implements SiteReportHandler {
    private static Logger logger = Logger.getLogger(SiteReportPublisher.class);

    public static final String RAWDATA_REPORT_PUBLISHED_NOTIFICATION = SiteReportPublisher.class.getName() + ".rawDataReportPublished";
    private NotificationCenter notificationCenter;
    private InterestingSitesCache interestingSitesCache;
    private SiteReportMessageFormatter formatter;

    @Resource(mappedName = "java:jboss/wmq/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:jboss/eventsTopic")
    private Topic topic;

    public SiteReportPublisher() {
    }

    @Inject
    public SiteReportPublisher(NotificationCenter notificationCenter, InterestingSitesCache interestingSitesCache, SiteReportMessageFormatter formatter) {
        this.notificationCenter = notificationCenter;
        this.interestingSitesCache = interestingSitesCache;
        this.formatter = formatter;
    }

    @Override
    public boolean didHandleSiteReport(SiteReport siteReport) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        long now = System.currentTimeMillis();
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(topic);

            TextMessage textMessage = formatter.formatMessageWithSiteReport(session.createTextMessage(), siteReport, interestingSitesCache);
            if (logger.isTraceEnabled())
                traceMessage(textMessage);
            producer.send(textMessage, DeliveryMode.PERSISTENT, textMessage.getJMSPriority(), producer.getTimeToLive());
            notificationCenter.postNotificationAsync(RAWDATA_REPORT_PUBLISHED_NOTIFICATION, System.currentTimeMillis() - now);
            return true;
        } catch (JMSException e) {
            logger.error("Unable to publish message", e);
            return false;
        } finally {
            if (producer != null)
                try {
                    producer.close();
                } catch (JMSException eatIt) {
                }
            if (session != null)
                try {
                    session.close();
                } catch (JMSException eatIt) {
                }
            if (connection != null)
                try {
                    connection.close();
                } catch (JMSException eatIt) {
                }   
        }
    }
    
    @SuppressWarnings("unchecked")
    private void traceMessage(Message message) throws JMSException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Publishing Event\n");
        stringBuilder.append("Message Header: \n");
        for (Enumeration<String> enumeration = message.getPropertyNames(); enumeration.hasMoreElements(); ) {
            String name = enumeration.nextElement();
            stringBuilder.append("    ")
                .append(name)
                .append(" = ")
                .append(message.getObjectProperty(name))
                .append('\n');
         }
         stringBuilder.append("Payload:\n");
         stringBuilder.append(((TextMessage)message).getText());
         logger.trace(stringBuilder.toString());
    }
}
