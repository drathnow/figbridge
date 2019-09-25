package zedi.pacbridge.app.publishers;

import java.util.Enumeration;

import javax.annotation.Resource;
import javax.ejb.Stateless;
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

import zedi.pacbridge.app.events.Event;

@Stateless
public class EventPublisher implements EventHandler {
    private static Logger logger = Logger.getLogger(EventPublisher.class);
    
    public static final String TIME_TO_LIVE_PROPERTY_NAME = "eventPublisher.defaultTimeToLiveMilliseconds";
    public static final String EVENT_NAME_KEY_NAME = "eventName";
    public static final String EVENT_QUALIFIER_KEY_NAME = "eventQualifier";
    public static final String IP_ADDRESS_KEY_NAME = "ipAddress";
    public static final int NORMAL_PRIORITY = 4;
    public static final int HIGH_PRIORITY = 5;

    @Resource(mappedName = "java:jboss/wmq/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:jboss/eventsTopic")
    private Topic topic;

    public EventPublisher() {
    }

    @Override
	public void publishEvent(Event event)  {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(topic);

            TextMessage textMessage = session.createTextMessage();
            textMessage.clearBody();
            textMessage.clearProperties();
            textMessage.setStringProperty(EVENT_QUALIFIER_KEY_NAME, event.getEventQualifier().getName());
            textMessage.setStringProperty(EVENT_NAME_KEY_NAME, event.getEventName().getName());
            if (event.getEventId() != null)
                textMessage.setJMSCorrelationID(event.getEventId().toString());
            textMessage.setText(event.asXmlString());
            if (logger.isTraceEnabled())
                traceMessage(textMessage);
            producer.send(textMessage, DeliveryMode.PERSISTENT, HIGH_PRIORITY, producer.getTimeToLive());
        } catch (JMSException e) {
            logger.error("Unable to publish message", e);
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
