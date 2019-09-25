package zedi.pacbridge.msg;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;


public class MessageListenerContainerTest extends BaseTestCase {

    public static final String SUBSCRIPTION_NAME = "foo";

    @Mock
    private Session session;
    @Mock
    private Connection connection;
    @Mock
    private MessageListener messageListener;
    @Mock
    private Topic topic;
    @Mock
    private SessionWrapperFactory sessionWrapperFactory;

    @Test
    public void shouldRollbackTransactionWhenOnMessageCalledForTransactedSessionWithException() throws Exception {
        Message message = mock(Message.class);
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);

        when(connection.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createConsumer(topic, null)).thenReturn(messageConsumer);
        when(sessionWrapper.isCommitted()).thenReturn(false);
        doThrow(new JMSException("")).when(sessionWrapper).commit();
        
        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, true, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);
        container.start(connection);

        container.onMessage(message);

        verify(sessionWrapper).reset();
        verify(messageListener).onMessage(eq(message));
        verify(sessionWrapper).commit();
        verify(sessionWrapper).rollback();
    }
    
    @Test
    public void shouldProcessMessageWhenOnMessageCalledForTransactedSession() throws Exception {
        Message message = mock(Message.class);
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);

        when(connection.createSession(true, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createConsumer(topic, null)).thenReturn(messageConsumer);
        when(sessionWrapper.isCommitted()).thenReturn(false);

        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, true, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);
        container.start(connection);

        container.onMessage(message);

        verify(sessionWrapper).reset();
        verify(messageListener).onMessage(eq(message));
        verify(sessionWrapper).commit();
    }

    @Test
    public void shouldProcessMessageWhenOnMessageCalledForNontransactedSession() throws Exception {
        Message message = mock(Message.class);
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);

        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createConsumer(topic, null)).thenReturn(messageConsumer);

        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, false, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);
        container.start(connection);

        container.onMessage(message);

        verify(sessionWrapper).reset();
        verify(messageListener).onMessage(eq(message));
        verify(sessionWrapper, never()).commit();
    }

    //

    @Test
    public void shouldCloseMessageListener() throws Exception {
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);

        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createConsumer(topic, null)).thenReturn(messageConsumer);

        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, false, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);
        container.start(connection);

        container.close();

        verify(messageConsumer).close();
        verify(sessionWrapper).close();
    }

    @Test
    public void shouldStartNonDurableSubscriber() throws Exception {
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);

        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createConsumer(topic, null)).thenReturn(messageConsumer);

        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, false, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);

        container.start(connection);

        verify(connection).createSession(eq(false), eq(Session.AUTO_ACKNOWLEDGE));
        verify(sessionWrapperFactory).newSessionWrapper(eq(session));
        verify(session).createConsumer(eq(topic), (String)eq(null));
        verify(session, never()).createDurableSubscriber(eq(topic), eq(SUBSCRIPTION_NAME));
    }

    @Test
    public void shouldStartDurableSubscriber() throws Exception {
        SessionWrapper sessionWrapper = mock(SessionWrapper.class);
        TopicSubscriber messageConsumer = mock(TopicSubscriber.class);

        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(sessionWrapperFactory.newSessionWrapper(eq(session))).thenReturn(sessionWrapper);
        when(session.createDurableSubscriber(topic, SUBSCRIPTION_NAME)).thenReturn(messageConsumer);

        MessageListenerContainer container = new MessageListenerContainer(messageListener, topic, SUBSCRIPTION_NAME, false, null);
        container.setSessionWrapperFactory(sessionWrapperFactory);

        container.start(connection);

        verify(connection).createSession(eq(false), eq(Session.AUTO_ACKNOWLEDGE));
        verify(sessionWrapperFactory).newSessionWrapper(eq(session));
        verify(session).createDurableSubscriber(eq(topic), eq(SUBSCRIPTION_NAME));
        verify(session, never()).createConsumer(eq(topic), (String)eq(null));
    }
}
