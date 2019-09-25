package zedi.pacbridge.msg;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.NamingException;

public interface JmsImplementor {
    public void setClientId(String cliendId);
    public void initialize() throws Exception;
    public JmsServerReconnector serverReconnector();
    public Connection createConnection() throws JMSException, NamingException;
    public Topic createTopic(String topicName) throws NamingException, JMSException;
    public Destination createDestination(String destinationName) throws NamingException, JMSException;
}
