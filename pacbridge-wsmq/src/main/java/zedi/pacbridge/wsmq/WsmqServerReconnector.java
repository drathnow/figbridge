package zedi.pacbridge.wsmq;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.msg.JmsServerReconnector;

/**
 * Implemenation of the <code>JmsServerReconnector</code> interface to reestablish a connection
 * to the JMS server, if it is lost.
 * 
 */
public class WsmqServerReconnector implements JmsServerReconnector { 
    private static Logger logger = LoggerFactory.getLogger(WsmqServerReconnector.class.getName());

    private WsmqJmsImplementator implemenation;
    
    public WsmqServerReconnector(WsmqJmsImplementator implemenation) {
        this.implemenation = implemenation;
    }

    @Override
    public boolean isConnectionReestabilshed() {
        Connection connection = null;
        try {
            connection = implemenation.createConnection();
            return true;
        } catch (Exception e) {
            logger.error("Unable to create connection to JMS server", e);
            return false;
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (JMSException eatIt) {
                }
        }
    }
}
