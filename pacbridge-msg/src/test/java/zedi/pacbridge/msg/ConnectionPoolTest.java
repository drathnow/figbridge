package zedi.pacbridge.msg;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.jms.Connection;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;


public class ConnectionPoolTest extends BaseTestCase {

    @Mock
    private Lock lock;
    @Mock
    private Condition condition;
    @Mock
    private PriorityQueue<ConnectionWrapper> thePool;
    @Mock
    private JmsImplementor jmsImplementation;
    @Mock
    private ConnectionWrapperFactory connectionWrapperFactory;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(lock.newCondition()).thenReturn(condition);
    }
    
    @Test
    public void shouldNotAddExpiredConnectionWrapper() throws Exception {
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);

        when(connectionWrapper.isExpired()).thenReturn(true);
        when(connectionWrapper.isBad()).thenReturn(false);
        when(thePool.size()).thenReturn(ConnectionPool.DEFAULT_CONNECTION_POOL_SIZE);
        
        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);
        connectionPool.freeConnection(connectionWrapper);

        verify(thePool, never()).add(eq(connectionWrapper));
        verify(connectionWrapper).closeConnection();
        verify(connectionWrapper, never()).close();
    }
    
    @Test
    public void shouldWaitForConnection() throws Exception {
        System.setProperty(ConnectionPool.CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME, "2");
        Integer activeConnectionCount = 2;
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
        Connection connection = mock(Connection.class);

        given(jmsImplementation.createConnection()).willReturn(connection);
        given(thePool.isEmpty())
            .willReturn(true)
            .willReturn(true)
            .willReturn(true)
            .willReturn(true)
            .willReturn(false);
        given(thePool.poll()).willReturn(connectionWrapper);
        given(connectionWrapper.isBad()).willReturn(false);
        
        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);
        given(connectionWrapperFactory.newConnectionWrapper(connection, connectionPool)).willReturn(connectionWrapper);
        
        for (int i = 0; i < activeConnectionCount; i++)
            connectionPool.getConnection();
        System.out.println("Active: "+ connectionPool.getActiveConnectionCount());
        verify(condition, never()).await(anyLong(), any(TimeUnit.class));
        
        connectionPool.getConnection();
        
        verify(condition).await(eq((long)ConnectionPool.DEFAULT_WAIT_SECONDS), eq(TimeUnit.SECONDS));
        verify(jmsImplementation, times(2)).createConnection();
    }
    
    @Test
    public void shouldWaitForConnectionButCreateNewOneIfNoneIsReturnedAfterWaitPeriod() throws Exception {
        System.setProperty(ConnectionPool.CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME, "2");
        Integer activeConnectionCount = 2;
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
        Connection connection = mock(Connection.class);

        given(jmsImplementation.createConnection()).willReturn(connection);
        given(thePool.isEmpty())
            .willReturn(true)
            .willReturn(true)
            .willReturn(false)
            .willReturn(true);
        given(thePool.poll()).willReturn(connectionWrapper);
        given(connectionWrapper.isBad()).willReturn(false);
        
        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);
        given(connectionWrapperFactory.newConnectionWrapper(connection, connectionPool)).willReturn(connectionWrapper);
        for (int i = 0; i < activeConnectionCount; i++)
            connectionPool.getConnection();
        verify(condition, never()).await(anyLong(), any(TimeUnit.class));
        
        connectionPool.getConnection();
        
        verify(condition).await(eq((long)ConnectionPool.DEFAULT_WAIT_SECONDS), eq(TimeUnit.SECONDS));
        verify(jmsImplementation, times(4)).createConnection();
    }

    @Test
    public void shouldReturnGoodConnection() throws Exception {
        System.setProperty(ConnectionPool.CONNECTION_POOL_MAX_SIZE_PROPERTY_NAME, "2");
        Integer activeConnectionCount = 2;
        ConnectionWrapper goodConnectionWrapper = mock(ConnectionWrapper.class);
        ConnectionWrapper badConnectionWrapper = mock(ConnectionWrapper.class);

        when(thePool.isEmpty()).thenReturn(false);
        when(thePool.poll())
            .thenReturn(badConnectionWrapper)
            .thenReturn(goodConnectionWrapper);
        when(badConnectionWrapper.isBad()).thenReturn(true);
        when(goodConnectionWrapper.isBad()).thenReturn(false);
        
        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);
        for (int i = 0; i < activeConnectionCount; i++)
            connectionPool.getConnection();
        
        Connection result = connectionPool.getConnection();

        assertSame(goodConnectionWrapper, result);
        verify(badConnectionWrapper).closeConnection();
    }

    @Test
    public void shouldCloseConnectionMarkedBadWhenFreed() throws Exception {
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);

        when(connectionWrapper.isBad()).thenReturn(true);
        when(connectionWrapper.isExpired()).thenReturn(false);
        when(thePool.size()).thenReturn(0);

        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);

        connectionPool.freeConnection(connectionWrapper);
        
        verify(connectionWrapper).closeConnection();
        verify(thePool, never()).add(eq(connectionWrapper));
    }

    @Test
    public void shouldCloseConnectinoWhenFreeConnectionCalledAndPoolIsWhenFull() throws Exception {
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);

        when(connectionWrapper.isBad()).thenReturn(false);
        when(connectionWrapper.isExpired()).thenReturn(false);
        when(thePool.size()).thenReturn(ConnectionPool.DEFAULT_CONNECTION_POOL_MAX_SIZE);

        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);

        connectionPool.freeConnection(connectionWrapper);
        
        verify(connectionWrapper).closeConnection();
        verify(thePool, never()).add(eq(connectionWrapper));
    }

    @Test
    public void shouldReturnConnectionToPoolWhenFreeConnectionCalledWithGoodConnection() throws Exception {
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);

        when(connectionWrapper.isBad()).thenReturn(false);
        when(connectionWrapper.isExpired()).thenReturn(false);
        when(thePool.size()).thenReturn(0);
        when(thePool.contains(connectionWrapper)).thenReturn(false);

        ConnectionPool connectionPool = new ConnectionPool(jmsImplementation, thePool, lock, connectionWrapperFactory);

        connectionPool.freeConnection(connectionWrapper);
        
        verify(connectionWrapper, never()).closeConnection();
        verify(thePool).add(eq(connectionWrapper));
        verify(condition).signal();
    }
}
