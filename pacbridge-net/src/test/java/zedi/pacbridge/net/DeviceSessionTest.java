package zedi.pacbridge.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import zedi.pacbridge.net.annotations.Async;
import zedi.pacbridge.test.BaseTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DeviceSession.class)
public class DeviceSessionTest extends BaseTestCase {
    private static final Integer SESSION_ID = 21;
    
    @Mock
    private SessionManager sessionManager;
    @Mock
    private Message message;
    @Mock
    private MessageListener messageListener;
    @Mock
    private LinkedBlockingDeque<Message> messageQueue;
    @Mock
    private Logger logger;
    
    @Test
    public void shouldSetAsyncMessageListener() throws Exception {
        MessageListenerProxy proxy = mock(MessageListenerProxy.class);
        AsynTestListener asyncListener = new AsynTestListener();
        
        whenNew(MessageListenerProxy.class)
            .withArguments(asyncListener)
            .thenReturn(proxy);
        
        DeviceSession session = new DeviceSession(SESSION_ID, sessionManager, messageQueue);
        session.setMessageListener(asyncListener);
        session.handleRecievedMessage(message);
        verify(proxy).handleMessage(message);
        verifyNew(MessageListenerProxy.class).withArguments(asyncListener);
    }
    
    @Test(expected = RuntimeException.class)
    public void shouldShouldPukeIfYouTryToSetMessageListenerTwice() throws Exception {
        MessageListener anotherListener = mock(MessageListener.class);
        Session session = new DeviceSession(SESSION_ID, sessionManager, messageQueue);
        session.setMessageListener(messageListener);
        session.setMessageListener(anotherListener);
    }
    
    @Test
    public void shouldReceiveNewMessageThroughMessageListenerIfMessageListernSetAfterMessageArrive() throws Exception {
        Message message2 = mock(Message.class);
        given(messageQueue.size())
            .willReturn(2)
            .willReturn(1)
            .willReturn(0);
        given(messageQueue.poll())
            .willReturn(message)
            .willReturn(message2)
            .willReturn(null);
        Session session = new DeviceSession(SESSION_ID, sessionManager, messageQueue);
        session.setMessageListener(messageListener);
        verify(messageListener).handleMessage(message);
        verify(messageListener).handleMessage(message2);
    }
    
    @Test
    public void shouldQueueMessageIfMessageListenerIsNotPresent() throws Exception {
        DeviceSession session = new DeviceSession(SESSION_ID, sessionManager, messageQueue);
        session.handleRecievedMessage(message);
        verify(messageQueue).offer(message);
    }
    
    @Test
    public void shouldPassMessageToMessageListenerIfPresent() throws Exception {
        DeviceSession session = new DeviceSession(SESSION_ID, sessionManager, messageQueue);
        session.setMessageListener(messageListener);
        session.handleRecievedMessage(message);
        verify(messageListener).handleMessage(message);
        verify(messageQueue, never()).offer(message);
    }
    
    @Async
    public static class AsynTestListener implements MessageListener {

        @Override
        public void handleMessage(Message message) {
        }
    }
}
