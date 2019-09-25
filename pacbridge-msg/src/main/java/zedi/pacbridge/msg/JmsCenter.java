package zedi.pacbridge.msg;

import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.naming.NamingException;

import zedi.pacbridge.msg.annotations.JmsImplParam;
import zedi.pacbridge.utl.NotificationCenter;

@ApplicationScoped
public class JmsCenter {
    /**
     * Notification posted to the {@link zedi.pacbridge.utl.NotificationCenter} when connection
     * the the JMS server is lost.
     */
    public static final String CONNECTION_LOST_NOTIFICATION = "jmsServerConnectionLost";

    /**
     * Notification posted to the {@link zedi.pacbridge.utl.NotificationCenter} when connection
     * the the JMS server is regained.
     */
    public static final String CONNECTION_RECONNECTED_NOTIFICATION = "jmsServerReconnected";

    private JmsImplementor jmsImplementor;
    private ConnectionPool connectionPool;
    private MessageListenerManager messageListenerManager;
    private ExceptionListener exceptionListener;
    
    JmsCenter(ConnectionPool connectionPool, JmsImplementor implementor, NotificationCenter notificationCenter) {
        this.connectionPool = connectionPool;
        this.jmsImplementor = implementor;
        try {
            this.jmsImplementor.initialize();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize JMSImplementor.", e);
        }
        this.exceptionListener = new ExceptionListener(implementor.serverReconnector(), notificationCenter);
        this.messageListenerManager = new MessageListenerManager(this, notificationCenter);
    }

    @Inject
    public JmsCenter(@JmsImplParam JmsImplementor jmsImplementor, NotificationCenter notificationCenter) {
        this(new ConnectionPool(jmsImplementor), jmsImplementor, notificationCenter);
    }

    public JmsCenter() {
    }
    
    public Connection getConnection() throws JMSException, NamingException, TimeoutException {
        Connection connection = connectionPool.getConnection();
        connection.setExceptionListener(exceptionListener);
        return connection;
    }

    public Destination getDestination(String destinationName) throws JMSException, NamingException {
        return jmsImplementor.createDestination(destinationName);
    }

    public Topic getTopic(String topicName) throws JMSException, NamingException {
        return jmsImplementor.createTopic(topicName);
    }

    public int getCreatedConnectionCount() {
        return connectionPool.getCreatedConnectionCount();
    }
    
    public int getActiveConnectionCount() {
        return connectionPool.getActiveConnectionCount();
    }
    
    public int getCurrentPoolSize() {
        return connectionPool.getCurrentPoolSize();
    }
    
    public void registerMessageListener(MessageListener messageListener, String destinationName, String subscriptionName, boolean isTransacted) {
        messageListenerManager.registerMessageListener(messageListener, destinationName, subscriptionName, isTransacted);
    }
    
    public void registerMessageListener(MessageListener messageListener, String destinationName, boolean isTransacted) {
        messageListenerManager.registerMessageListener(messageListener, destinationName, isTransacted);
    }
    
    public void registerMessageListener(String lookupName, String destinationName, boolean isTransacted) {
        messageListenerManager.registerMessageListener(lookupName, destinationName, isTransacted);
    }

    public void unregisterMessageListener(MessageListener messageListener) {
        messageListenerManager.unregisterMessageListener(messageListener);
    }

    public void unregisterMessageListener(Class<? extends MessageListener> listernClass) {
        messageListenerManager.unregisterMessageListenerClass(listernClass);
    }
}
