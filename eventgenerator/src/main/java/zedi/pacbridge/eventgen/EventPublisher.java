package zedi.pacbridge.eventgen;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import zedi.pacbridge.app.events.Event;

public class EventPublisher {
    private static Logger logger = Logger.getLogger(EventPublisher.class.getName());

    private Connection connection;
    private Destination destination;

    public EventPublisher() {
    }
    
    public EventPublisher(Connection connection, Destination destination) {
        this.connection = connection;
        this.destination = destination;
    }

    public void publishStingAsEventString(String eventString) {
        try {
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            message.setText(eventString);

            logger.info("Publishing string as event: \n" + eventString);
            String foo = producer.getDestination().toString();
            producer.send(message);
            session.commit();

            producer.close();
            session.close();
        } catch (JMSException e) {
            logger.error("Unable to publish message", e);
        }
    }

    public void publishEvent(Event event, boolean logEvent) {
        try {
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            message.setText(event.asXmlString());

            if (logEvent)
                logger.info("Publishing event: \n" + event.asXmlString());
            producer.send(message);
            session.commit();

            producer.close();
            session.close();
        } catch (JMSException e) {
            logger.error("Unable to publish message", e);
        }
    }
    
    public void publishEvent(Event event) {
        publishEvent(event, true);
    }
}
