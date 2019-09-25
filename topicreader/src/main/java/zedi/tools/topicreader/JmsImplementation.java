package zedi.tools.topicreader;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.NamingException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;


public class JmsImplementation  {
    public static final Integer DEFAULT_PORT_NUMBER = 1414;
    public static final String DEFAULT_CHANNEL_NAME = "SYSTEM.DEF.SVRCONN";
    
    private String queueManagerName;
    private String hostName;
    private String clientId;
    private String channelName = DEFAULT_CHANNEL_NAME;
    private Integer portNumber = DEFAULT_PORT_NUMBER;
    private MQContextFactory contextFactory;

    public JmsImplementation(String hostname, String queueManagerName) throws JMSException {
        this(hostname, queueManagerName, DEFAULT_CHANNEL_NAME, DEFAULT_PORT_NUMBER);
    }

    public JmsImplementation(String hostName, String queueManagerName, String channelName, Integer portNumber) throws JMSException {
        this.queueManagerName = queueManagerName;
        this.hostName = hostName;
        this.channelName = channelName;
        this.portNumber = portNumber;
        this.contextFactory = new MQContextFactory();
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public Connection createConnection() throws JMSException, NamingException {
        Connection connection = contextFactory.createConnection();
        if (clientId != null && connection.getClientID() == null)
            connection.setClientID(clientId);
        return connection;
    }

    public Destination createDestination(String destinationName) throws NamingException, JMSException {
        return contextFactory.createDestination(destinationName);
    }
    
    public MQQueueManager queueManager() throws MQException {
        Properties properties = new Properties();
        properties.put(MQEnvironment.hostname, hostName);
        properties.put(MQEnvironment.port, new Integer(portNumber));
        properties.put(MQEnvironment.channel, channelName);
        return new MQQueueManager(queueManagerName, properties);
    }
    
    class MQContextFactory {

        private JmsFactoryFactory jmsFactory;
        private JmsConnectionFactory connectionFactory;
        
        public MQContextFactory() throws JMSException {
            jmsFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            connectionFactory = jmsFactory.createConnectionFactory();
            connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, hostName);
            connectionFactory.setIntProperty(WMQConstants.WMQ_PORT, portNumber);
            connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, channelName);
            connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManagerName);
        }
        
        public Connection createConnection() throws JMSException {
            return connectionFactory.createConnection();
        }

        public Topic createTopic(String topicName) throws JMSException {
            return (Topic)jmsFactory.createTopic(topicName);
        }

        public String toString() {
            return "MQContextFactory - " + hostName + ":" + queueManagerName + ":" + channelName + ":" + portNumber;
        }

        public Destination createDestination(String destinationName) throws NamingException, JMSException {
            return destinationName.toLowerCase().startsWith("queue://") 
                ? jmsFactory.createDestination(JmsConstants.ADMIN_QUEUE_DOMAIN, destinationName) 
                        : jmsFactory.createDestination(JmsConstants.ADMIN_TOPIC_DOMAIN, destinationName);

        }
    }
}
