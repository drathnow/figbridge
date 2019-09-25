package zedi.pacbridge.wsmq;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.msg.JmsImplementor;
import zedi.pacbridge.msg.JmsServerReconnector;
import zedi.pacbridge.msg.annotations.JmsImplementation;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;


/**
 * The WSMQJmsImplemenation class provide access to a WebSphere Message Queue message server.
 * The message broker can be accessed by either a set of parameters that define the conection information
 * or via a server URL containing that points to a location or LDAP server where alsl the conection
 * information can be obtained.
 */
@JmsImplementation(name = "WSMQ")
public class WsmqJmsImplementator implements JmsImplementor {
    private static Logger logger = LoggerFactory.getLogger(WsmqJmsImplementator.class.getName());
    private static final String FILE_CONTEXT_FACTORY_NAME = "com.sun.jndi.fscontext.RefFSContextFactory";

    public static final Integer DEFAULT_PORT_NUMBER = 1414;
    public static final String DEFAULT_CHANNEL_NAME = "SYSTEM.DEF.SVRCONN";
    public static final String DEFAULT_CONNECTION_FACTORY_NAME = "ConnectionFactory";
    
    private String queueManagerName;
    private String hostName;
    private String serverURL;
    private String clientId;
    private String username;
    private String password;
    private String connectionFactoryName = DEFAULT_CONNECTION_FACTORY_NAME;
    private String channelName = DEFAULT_CHANNEL_NAME;
    private Integer portNumber = DEFAULT_PORT_NUMBER;
    private ContextFactory contextFactory;

    public WsmqJmsImplementator() {
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "JMS Implemenation: WSMQ";
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }
    
    @Override
    public void initialize() throws Exception {
        contextFactory = new MQContextFactory();
    }
    
    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    @Override
    public Connection createConnection() throws JMSException, NamingException {
        Connection connection = contextFactory.createConnection();
        if (clientId != null && connection.getClientID() == null)
            connection.setClientID(clientId);
        return connection;
    }

    @Override
    public JmsServerReconnector serverReconnector() {
        return new WsmqServerReconnector(this);
    }
    
    @Override
    public Topic createTopic(String topicName) throws NamingException, JMSException {
        return contextFactory.createTopic(topicName);
    }
    
    @Override
    public Destination createDestination(String destinationName) throws NamingException, JMSException {
        return contextFactory.createDestination(destinationName);
    }
    
    public MQQueueManager queueManager() throws MQException {
        Properties properties = new Properties();
        properties.put(MQEnvironment.hostname, hostName);
        properties.put(MQEnvironment.port, new Integer(portNumber));
        properties.put(MQEnvironment.channel, channelName);
        if (username != null)
            properties.put(MQEnvironment.userID, username);
        if (password != null)
            properties.put(MQEnvironment.password, password);
        return new MQQueueManager(queueManagerName, properties);
    }
    
    private abstract class ContextFactory {
        Properties properties = new Properties();
        public abstract Connection createConnection() throws NamingException, JMSException;
        public abstract Topic createTopic(String topicName) throws NamingException, JMSException;
        public abstract Destination createDestination(String topicName) throws NamingException, JMSException;
        
        protected InitialContext initialContext() throws NamingException {
            return new InitialContext(properties);
        }
    }
    
    class MQContextFactory extends ContextFactory {

        private JmsFactoryFactory jmsFactory;
        private JmsConnectionFactory connectionFactory;
        
        public MQContextFactory() throws JMSException {
            jmsFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            connectionFactory = jmsFactory.createConnectionFactory();

            logger.debug("WSMQ JMS implemenation initialized with context factory MQContextFactory");
            logger.debug("            Host Name: " + hostName);
            logger.debug("   Queue Manager Name: " + queueManagerName);
            logger.debug("         Channel Name: " + channelName);
            logger.debug("          Port Number: " + portNumber);
            connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, hostName);
            connectionFactory.setIntProperty(WMQConstants.WMQ_PORT, portNumber);
            connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, channelName);
            connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManagerName);
        }
        
        @Override
        public Connection createConnection() throws JMSException {
            logger.debug("Creating new connection");
            return connectionFactory.createConnection();
        }

        @Override
        public Topic createTopic(String topicName) throws JMSException {
            return (Topic)jmsFactory.createTopic(topicName);
        }

        @Override
        public String toString() {
            return "MQContextFactory - " + hostName + ":" + queueManagerName + ":" + channelName + ":" + portNumber;
        }

        @Override
        public Destination createDestination(String destinationName) throws NamingException, JMSException {
            return destinationName.toLowerCase().startsWith("topic://") 
                ? jmsFactory.createDestination(JmsConstants.ADMIN_TOPIC_DOMAIN, destinationName) 
                        : jmsFactory.createDestination(JmsConstants.ADMIN_QUEUE_DOMAIN, destinationName);

        }
    }
    
    class FileContextFactory extends ContextFactory {

        public FileContextFactory(String url) {
            logger.debug("WSMQ JMS implemenation initialized with context factory FileContextFactory");
            logger.debug("    Server URL: " + serverURL);
            properties.put(Context.INITIAL_CONTEXT_FACTORY, FILE_CONTEXT_FACTORY_NAME);
            properties.put(Context.PROVIDER_URL, serverURL);
            properties.put(Context.REFERRAL, "throw");
        }
        
        @Override
        public Connection createConnection() throws NamingException, JMSException {
            InitialContext initialContext = initialContext();
            Object lookup = initialContext.lookup(connectionFactoryName);
            ConnectionFactory connectionFactory = (ConnectionFactory)lookup;
            return connectionFactory.createConnection();
        }

        @Override
        public Topic createTopic(String topicName) throws NamingException {
            InitialContext initialContext = initialContext();
            return (Topic)initialContext.lookup(topicName);
        }

        @Override
        public Destination createDestination(String topicName) throws NamingException, JMSException {
            return (Destination)initialContext().lookup(topicName);
        }

        @Override
        public String toString() {
            return "FileContextFactory - " + serverURL;
        }
    }
}
