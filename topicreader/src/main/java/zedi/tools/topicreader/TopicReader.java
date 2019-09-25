package zedi.tools.topicreader;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.NamingException;

public class TopicReader {

    public TopicReader(JmsImplementation implementator, String topicName, MessageListener messageListener) throws JMSException, NamingException {
        Destination destination = implementator.createDestination(topicName);
        Connection connection = implementator.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(messageListener);
        connection.start();
    }
}
