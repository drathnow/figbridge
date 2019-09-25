package zedi.pacbridge.msg;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.IntegerSystemProperty;


class ConnectionWrapper implements Connection, ExceptionListener, Comparable<ConnectionWrapper> {

    private static Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);
    
    IntegerSystemProperty connectionTimeoutMinutes = new IntegerSystemProperty(ConnectionPool.CONNECTION_TIMEOUT_MINUTES_PROPERTY_NAME, ConnectionPool.DEFAULT_CONNECTION_TIMEOUT_MINUTES);

    Connection theConnection;
    ConnectionPool connectionPool;
    boolean bad;
    long creationTime;
    long connectionTimeout;
    int createdSessionCount;
    
    public ConnectionWrapper(Connection connection, ConnectionPool connectionPool) {
        this.creationTime = System.currentTimeMillis();
        this.theConnection = connection;
        this.connectionPool = connectionPool;
        this.connectionTimeout = TimeUnit.MINUTES.toMillis(connectionTimeoutMinutes.currentValue());
        try {
            this.theConnection.setExceptionListener(this);
        } catch (JMSException e) {
        }
    }

    @Override
    public void close() throws JMSException {
        connectionPool.freeConnection(this);
    }

    /**
     * Exception handler for the wrapped Connection object. If an unhandled exception is caught,
     * this wrapper will be maked as bad and the current connection will be closed.  While marking
     * this wrapper as bad and closing the connection may seem like "double closing" we want to
     * make sure everything is cleaned up in the event this wrapper is not inserted back
     * into the connection pool. 
     */
    @Override
    public void onException(JMSException exception) {
        logger.error("Unhandled JMS exception encountered", exception);
        bad = true;
        closeConnection();
    }
    
    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages3) throws JMSException {
        return theConnection.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages3);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return theConnection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public Session createSession(boolean isTransacted, int acknowledgementMode) throws JMSException {
        createdSessionCount++;
        return theConnection.createSession(isTransacted, acknowledgementMode);
    }

    @Override
    public String getClientID() throws JMSException {
        return theConnection.getClientID();
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return theConnection.getExceptionListener();
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return theConnection.getMetaData();
    }

    @Override
    public void setClientID(String arg0) throws JMSException {
        theConnection.setClientID(arg0);
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        theConnection.setExceptionListener(listener);
    }

    @Override
    public void start() throws JMSException {
        theConnection.start();
    }

    @Override
    public void stop() throws JMSException {
        theConnection.stop();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime >= connectionTimeout;
    }    

    boolean isWrappedConnectionClosed() {
        return theConnection == null;
    }

    public int getCreatedSessionCount() {
        return createdSessionCount;
    }

    void closeConnection() {
        if (theConnection != null) {
            try {
                theConnection.close();
                theConnection = null;
            } catch (JMSException e) {
            }
        }
    }
    
    boolean isBad() {
        return bad;
    }

    @Override
    public int compareTo(ConnectionWrapper otherConnectionWrapper) {
            return getCreatedSessionCount() == otherConnectionWrapper.getCreatedSessionCount() 
                    ? 0 : getCreatedSessionCount() > otherConnectionWrapper.getCreatedSessionCount() ? 1 : -1;
    }

	@Override
	public Session createSession(int sessionMode) throws JMSException {
		return theConnection.createSession(sessionMode);
	}

	@Override
	public Session createSession() throws JMSException {
		return theConnection.createSession();
	}

	@Override
	public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
		return theConnection.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
	}

	@Override
	public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
		return theConnection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
	}
}
