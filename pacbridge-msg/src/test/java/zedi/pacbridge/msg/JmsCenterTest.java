package zedi.pacbridge.msg;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;


public class JmsCenterTest extends BaseTestCase {

    private static final String DESTINATION_NAME = "foo";
    
    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private JmsImplementor jmsImplementation;
    @Mock
    private ConnectionPool connectionPool;
    @Mock
    private Map<String, Object> configMap;

    @Test
    public void shouldGetConnectionFromConnectionPool() throws Exception {
        ConnectionWrapper connectionWrapper1 = mock(ConnectionWrapper.class);
        ConnectionWrapper connectionWrapper2 = mock(ConnectionWrapper.class);
        
        given(connectionPool.getConnection())
            .willReturn(connectionWrapper1)
            .willReturn(connectionWrapper2);
        
        JmsCenter jmsCenter = new JmsCenter(connectionPool, jmsImplementation, notificationCenter);
        
        Connection createdConnection = jmsCenter.getConnection();
        
        assertSame(connectionWrapper2, createdConnection);
        verify(connectionPool, times(2)).getConnection();
        verify(jmsImplementation).initialize();
        verify(connectionWrapper1).setExceptionListener(any(ExceptionListener.class));
        verify(connectionWrapper2).setExceptionListener(any(ExceptionListener.class));
    }
    
    @Test
    public void shouldCreateDestinationFromJmsImplemenation() throws Exception {
        Destination destination = mock(Destination.class);
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
        
        given(jmsImplementation.createDestination(DESTINATION_NAME)).willReturn(destination);
        given(connectionPool.getConnection()).willReturn(connectionWrapper);
        
        JmsCenter jmsCenter = new JmsCenter(connectionPool, jmsImplementation, notificationCenter);
        
        Destination createdDestination = jmsCenter.getDestination(DESTINATION_NAME);
        
        assertSame(destination, createdDestination);
        verify(jmsImplementation).createDestination(eq(DESTINATION_NAME));
        verify(jmsImplementation).initialize();
    }
}
