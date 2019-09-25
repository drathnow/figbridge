package zedi.pacbridge.msg;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.naming.Context;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.test.JndiContextHelper;
import zedi.pacbridge.test.SingletonTestHelper;
import zedi.pacbridge.utl.GlobalExecutor;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;


public class MessageListenerManagerTest extends BaseTestCase {

    public static final String TOPIC_NAME = "topic";

    @Mock
    private Notification notification;
    @Mock
    private Context context;
    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private MessageListenerContainerFactory messageListenerContainerFactory;
    @Mock
    private GlobalExecutor executor;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SingletonTestHelper.replaceStaticInstance(GlobalExecutor.class, executor);
        JndiContextHelper.setContext(context);
    }
    
    @Override
    public void tearDown() throws Exception {
        SingletonTestHelper.replaceStaticInstance(GlobalExecutor.class, null);
        JndiContextHelper.setContext(null);
        super.tearDown();
    }
    
    @Test
    public void shouldRestartContainersWhenConnectionReconnectNotificationDetected() throws Exception {
        Destination destination = mock(Destination.class);
        MessageListener messageListener = mock(MessageListener.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        Connection oldConnection = mock(Connection.class);
        Connection newConnection = mock(Connection.class);
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        
        given(jmsCenter.getConnection()).willReturn(null);
        given(jmsCenter.getDestination(TOPIC_NAME)).willReturn(destination);
        given(container.getMessageListener()).willReturn(messageListener);
        given(jmsCenter.getConnection())
            .willReturn(oldConnection)
            .willReturn(newConnection);
        given(notification.getName()).willReturn(JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
        given(messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, null, false, null))
            .willReturn(container);
        
        ArgumentCaptor<Runnable> argument = ArgumentCaptor.forClass(Runnable.class);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        verify(jmsCenter).getConnection();        
        manager.registerMessageListener(messageListener, TOPIC_NAME, null, false);
        
        manager.handleNotification(notification);
        
        verify(executor).execute(argument.capture());
        argument.getValue().run();
        verify(jmsCenter, times(2)).getConnection();        
        verify(container).start(newConnection);
    }
    
    @Test
    public void shouldCloseAllContainersWhenConnectionLostNotificationDetected() throws Exception {
        Destination destination = mock(Destination.class);
        MessageListener messageListener = mock(MessageListener.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        Connection connection = mock(Connection.class);
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        
        given(jmsCenter.getConnection()).willReturn(null);
        given(jmsCenter.getDestination(TOPIC_NAME)).willReturn(destination);
        given(container.getMessageListener()).willReturn(messageListener);
        given(jmsCenter.getConnection()).willReturn(connection);
        given(notification.getName()).willReturn(JmsCenter.CONNECTION_LOST_NOTIFICATION);
        given(messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, null, false, null))
            .willReturn(container);
        
        ArgumentCaptor<Runnable> argument = ArgumentCaptor.forClass(Runnable.class);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        manager.registerMessageListener(messageListener, TOPIC_NAME, null, false);
        
        manager.handleNotification(notification);
        
        verify(executor).execute(argument.capture());
        verify(container, never()).close();
        verify(connection, never()).close();
        
        argument.getValue().run();
        verify(container).close();
        verify(connection).close();
    }
        
    @Test
    public void shouldUnregisterMessageListener() throws Exception {
        Destination destination = mock(Destination.class);
        MessageListener messageListener = mock(MessageListener.class);
        MessageListenerContainer messageListenerContainer = mock(MessageListenerContainer.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        
        given(jmsCenter.getConnection()).willReturn(null);
        given(jmsCenter.getDestination(TOPIC_NAME)).willReturn(destination);
        given(messageListenerContainer.getMessageListener()).willReturn(messageListener);
        given(messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, null, false, null))
            .willReturn(messageListenerContainer);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        
        manager.registerMessageListener(messageListener, TOPIC_NAME, null, false);
        manager.unregisterMessageListener(messageListener);
        
        verify(notificationCenter).addObserver(manager, JmsCenter.CONNECTION_RECONNECTED_NOTIFICATION);
        verify(notificationCenter).addObserver(manager, JmsCenter.CONNECTION_LOST_NOTIFICATION);
        verify(messageListenerContainer).close();
    }
    
    @Test
    public void shouldRegisterMessageListenerButNotStartIfConnectionIsNull() throws Exception {
        Destination destination = mock(Destination.class);
        MessageListener messageListener = mock(MessageListener.class);
        MessageListenerContainer messageListenerContainer = mock(MessageListenerContainer.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        
        when(jmsCenter.getConnection()).thenReturn(null);
        when(jmsCenter.getDestination(TOPIC_NAME)).thenReturn(destination);
        when(messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, null, false, null))
            .thenReturn(messageListenerContainer);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        
        manager.registerMessageListener(messageListener, TOPIC_NAME, null, false);
        
        verify(jmsCenter).getDestination(TOPIC_NAME);
        verify(messageListenerContainer, never()).start(any(Connection.class));
    }

    @Test
    public void shouldRegisterMessageListenerClassWithConnection() throws Exception {
        String name = "FOO";
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        MessageListenerContainer messageListenerContainer = mock(MessageListenerContainer.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        
        when(jmsCenter.getConnection()).thenReturn(connection);
        when(jmsCenter.getDestination(TOPIC_NAME)).thenReturn(destination);
        when(messageListenerContainerFactory.newMessageListenerContainer(any(DefaultMessageListener.class), eq(destination), eq(false), (String)eq(null)))
            .thenReturn(messageListenerContainer);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        manager.registerMessageListener(name, TOPIC_NAME, false);
        
        verify(jmsCenter).getDestination(TOPIC_NAME);
        verify(messageListenerContainer).start(connection);
    }
    
    @Test
    public void shouldRegisterMessageListenerWithConnection() throws Exception {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        MessageListener messageListener = mock(MessageListener.class);
        MessageListenerContainer messageListenerContainer = mock(MessageListenerContainer.class);
        JmsCenter jmsCenter = mock(JmsCenter.class);
        
        when(jmsCenter.getConnection()).thenReturn(connection);
        when(jmsCenter.getDestination(TOPIC_NAME)).thenReturn(destination);
        when(messageListenerContainerFactory.newMessageListenerContainer(messageListener, destination, null, false, null))
            .thenReturn(messageListenerContainer);
        
        MessageListenerManager manager = new MessageListenerManager(jmsCenter, messageListenerContainerFactory, notificationCenter);
        
        manager.registerMessageListener(messageListener, TOPIC_NAME, null, false);
        
        verify(jmsCenter).getDestination(TOPIC_NAME);
        verify(messageListenerContainer).start(connection);
    }
}