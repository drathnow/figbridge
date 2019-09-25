package zedi.pacbridge.msg;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.IntegerSystemProperty;


public class ConnectionPool {

    private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    
    public static final String CONNECTION_POOL_SIZE_PROPERTY_NAME = "jms.connectionPool.initialSize";
    public static final String CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME = "jms.connectionPool.maxSize";
    public static final String CONNECTION_POOL_MAX_WAIT_SECONDS_PROPERTY_NAME = "jms.connectionPool.maxWaitSeconds";
    public static final String CONNECTION_TIMEOUT_MINUTES_PROPERTY_NAME = "jms.connectionPool.connectionTimeoutMinutes";
    public static final int DEFAULT_CONNECTION_TIMEOUT_MINUTES = 10;    
    public static final int DEFAULT_CONNECTION_POOL_SIZE = 20;
    public static final int DEFAULT_CONNECTION_POOL_MAX_SIZE = 20;
    public static final int DEFAULT_WAIT_SECONDS = 5;

    private static final IntegerSystemProperty connectionPoolMaxSizeProperty = new IntegerSystemProperty(CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME, DEFAULT_CONNECTION_POOL_MAX_SIZE);
    private static final IntegerSystemProperty connectionPoolMaxWaitSecondsProperty = new IntegerSystemProperty(CONNECTION_POOL_MAX_WAIT_SECONDS_PROPERTY_NAME, DEFAULT_WAIT_SECONDS);
    
    private PriorityQueue<ConnectionWrapper> thePool = new PriorityQueue<ConnectionWrapper>(connectionPoolMaxSizeProperty.currentValue());
    private final Lock lock;
    private final Condition waitingForConnectionCondition;
    private JmsImplementor jmsImplementation;
    private int createdConnectionCount;
    private int activeConnectionCount;
    private int closedConnectionCount;
    private ConnectionWrapperFactory connectionWrapperFactory;

    ConnectionPool(JmsImplementor jmsImplementation, PriorityQueue<ConnectionWrapper> thePool, Lock lock, ConnectionWrapperFactory connectionWrapperFactory) {
        this.jmsImplementation = jmsImplementation;
        this.thePool = thePool;
        this.lock = lock;
        this.waitingForConnectionCondition = lock.newCondition();
        this.connectionWrapperFactory = connectionWrapperFactory;
    }
    
    public ConnectionPool(JmsImplementor jmsImplementation) {
        this(jmsImplementation, new PriorityQueue<ConnectionWrapper>(connectionPoolMaxSizeProperty.currentValue()), new ReentrantLock(), new ConnectionWrapperFactory());
    }

    public Connection getConnection() throws JMSException, NamingException {
        ConnectionWrapper connectionWrapper = null;
        lock.lock();
        try {
            while (connectionWrapper == null) {
                if (thePool.isEmpty()) {
                    if (activeConnectionCount == connectionPoolMaxSizeProperty.currentValue())
                        waitForFreeConnection();
                    else
                        connectionWrapper = newConnection();
                } else
                    connectionWrapper = goodConnectionFromPool();
            }

        } finally {
            lock.unlock();
        }

        return connectionWrapper;
    }

    public Integer getCreatedConnectionCount() {
        return createdConnectionCount;
    }

    public Integer getActiveConnectionCount() {
        return activeConnectionCount;
    }
    
    public Integer getClosedConnectionCount() {
        return closedConnectionCount;
    }

    public Integer getCurrentPoolSize() {
        return thePool.size();
    }

    private ConnectionWrapper goodConnectionFromPool() {
        ConnectionWrapper connectionWrapper = null;
        while (connectionWrapper == null && thePool.isEmpty() == false) {
            connectionWrapper = thePool.poll();
            if (connectionWrapper.isBad()) {
                closeConnection(connectionWrapper);
                connectionWrapper = null;
            }
        }
        return connectionWrapper;
    }

    private void waitForFreeConnection() throws JMSException, NamingException {
        if (thePool.isEmpty()) {
            try {
                waitingForConnectionCondition.await(connectionPoolMaxWaitSecondsProperty.currentValue(), TimeUnit.SECONDS);
                if (thePool.isEmpty())
                    thePool.add(newConnection());
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for connection", e);
            }
        }
    }
    private void closeConnection(ConnectionWrapper connectionWrapper) {
        connectionWrapper.closeConnection();
        activeConnectionCount--;
        closedConnectionCount++;
    }

    private ConnectionWrapper newConnection() throws JMSException, NamingException {
        ConnectionWrapper connectionWrapper;
        Connection connection = jmsImplementation.createConnection();
        logger.debug("Created new connection " + connection.getClass().getName());
        connectionWrapper = connectionWrapperFactory.newConnectionWrapper(connection, this);
        connection.start();
        createdConnectionCount++;
        activeConnectionCount++;
        return connectionWrapper;
    }
    
    void freeConnection(ConnectionWrapper connectionWrapper) {
        lock.lock();
        try {
            if (shouldCloseConnection(connectionWrapper))
                closeConnection(connectionWrapper);
            else if (thePool.contains(connectionWrapper) == false) {
                thePool.add(connectionWrapper);
                waitingForConnectionCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean shouldCloseConnection(ConnectionWrapper connectionWrapper) {
        return connectionWrapper.isBad() 
                || isPoolFull()
                || connectionWrapper.isExpired();
    }

    private boolean isPoolFull() {
        return thePool.size() >= connectionPoolMaxSizeProperty.currentValue();
    }
} 
