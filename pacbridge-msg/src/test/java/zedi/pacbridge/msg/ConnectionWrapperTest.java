package zedi.pacbridge.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;


public class ConnectionWrapperTest extends BaseTestCase {

    @Mock
    private Connection connection;
    
    @Test
    public void shouldIncrementCreatedSessionCount() throws Exception {
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection, null);
        connectionWrapper.createSession(false, 0);
        
        assertEquals(1, connectionWrapper.getCreatedSessionCount());
    }
    
    @Test
    public void shouldConstructCorrectly() throws Exception {
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection, null);
        assertEquals(System.currentTimeMillis(), connectionWrapper.creationTime, 1000);
        assertEquals(TimeUnit.MINUTES.toMillis(ConnectionPool.DEFAULT_CONNECTION_TIMEOUT_MINUTES), connectionWrapper.connectionTimeout, 1000);
    }
    
    @Test
    public void shouldTimeoutConnection() throws Exception {
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection, null);
        assertFalse(connectionWrapper.isExpired());
        
        connectionWrapper.creationTime = System.currentTimeMillis() - (TimeUnit.MINUTES.toMillis(ConnectionPool.DEFAULT_CONNECTION_TIMEOUT_MINUTES));
        assertTrue(connectionWrapper.isExpired());
    }
}