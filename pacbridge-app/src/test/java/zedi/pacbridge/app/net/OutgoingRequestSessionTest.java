package zedi.pacbridge.app.net;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestProcessor;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.MessageListener;
import zedi.pacbridge.net.Session;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;
import zedi.pacbridge.utl.Timer;

public class OutgoingRequestSessionTest extends BaseTestCase {
    private static final Integer TIMEOUT_SECONDS = 30;
    private static final Integer SEQ_ID = 100;
    private static final SiteAddress siteAddress = new NuidSiteAddress("1.2.3.4", 0);
    
    @Mock
    private DeviceConnection connection;
    @Mock
    private ThreadContext requester;
    @Mock
    private Message response;
    @Mock
    private OutgoingRequestSessionListener sessionListener;
    @Mock
    private Timer timer;
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
    
    private ArgumentCaptor<ThreadContextHandler> handlerCaptor = ArgumentCaptor.forClass(ThreadContextHandler.class);
    private ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    private ArgumentCaptor<MessageListener> messageListnerCaptor = ArgumentCaptor.forClass(MessageListener.class);
    
    @Test
    public void shouldFinishTimedOutMessage() throws Exception {
        Message message = mock(Message.class);
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestProcessor requestProcessor = mock(OutgoingRequestProcessor.class);
        Session session = mock(Session.class);
        FutureTimer future = mock(FutureTimer.class);
        
        given(session.nextSequenceNumber()).willReturn(SEQ_ID);
        given(connection.newSession()).willReturn(session);
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequest.getResponseTimeoutSeconds()).willReturn(TIMEOUT_SECONDS);
        given(outgoingRequest.outgoingRequestProcessor()).willReturn(requestProcessor);
        given(outgoingRequest.isCancelled()).willReturn(false);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID)).willReturn(message);
        given(requestProcessor.isExpected(response)).willReturn(true);
        given(requester.getTimer()).willReturn(timer);
        given(timer.schedule(any(Runnable.class), eq(1L), eq(TimeUnit.SECONDS))).willReturn(future);
        given(requestProcessor.hasMoreMessages()).willReturn(false);
        
        OutgoingRequestSession requestSession = new OutgoingRequestSession(outgoingRequest, connection, requester);
        requestSession.start();
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        
        // Test StartNextRequestState 
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(outgoingRequest).outgoingRequestProcessor();
        verify(connection).newSession();
        verify(session).setMessageListener(messageListnerCaptor.capture());
        verify(requestProcessor).starting();
        verify(requester, times(2)).requestTrap(handlerCaptor.capture());
        
        // Test SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(session).sendMessage(message, 0);
        verify(timer).schedule(runnableCaptor.capture(), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS));
        
        // Test WaitingForResponseState
        assertIsInState(requestSession, OutgoingRequestSession.WaitingForResponseState.class);
        runnableCaptor.getValue().run();
        verify(requestProcessor).forceFinished(ControlStatus.TIMED_OUT, OutgoingRequestSession.TIMEOUT_ERROR_MSG);
        
        assertIsInState(requestSession, OutgoingRequestSession.FinishProcessingRequestState.class);
    }
    
    @Test
    public void shouldCleanUpIfClosed() throws Exception {
        Message message = mock(Message.class);
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestProcessor requestProcessor = mock(OutgoingRequestProcessor.class);
        Session session = mock(Session.class);
        FutureTimer future = mock(FutureTimer.class);
        
        given(session.nextSequenceNumber()).willReturn(SEQ_ID);
        given(connection.newSession()).willReturn(session);
        given(outgoingRequest.getResponseTimeoutSeconds()).willReturn(TIMEOUT_SECONDS);
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequest.outgoingRequestProcessor()).willReturn(requestProcessor);
        given(outgoingRequest.isCancelled()).willReturn(false);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID)).willReturn(message);
        given(requestProcessor.isExpected(response)).willReturn(true);
        given(requester.getTimer()).willReturn(timer);
        given(timer.schedule(any(Runnable.class), eq(1L), eq(TimeUnit.SECONDS))).willReturn(future);
        given(requestProcessor.hasMoreMessages()).willReturn(false);
        
        OutgoingRequestSession requestSession = new OutgoingRequestSession(outgoingRequest, connection, requester);
        requestSession.setOutgoingRequestSessionListener(sessionListener);
        requestSession.start();
        
        // Test StartNextRequestState 
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(outgoingRequest).outgoingRequestProcessor();
        verify(connection).newSession();
        verify(session).setMessageListener(messageListnerCaptor.capture());
        verify(requestProcessor).starting();
        verify(requester, times(2)).requestTrap(handlerCaptor.capture());
        
        // Test SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(session).sendMessage(message, 0);
        verify(timer).schedule(runnableCaptor.capture(), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS));
        
        assertIsInState(requestSession, OutgoingRequestSession.WaitingForResponseState.class);
        requestSession.close();
        assertIsInState(requestSession, OutgoingRequestSession.ClosedState.class);
        runnableCaptor.getValue().run();
        verify(session).close();
        verify(requestProcessor).forceFinished(ControlStatus.FAILURE, OutgoingRequestSession.CLOSED_MSG);
        verify(sessionListener).sessionClosed(requestSession);
    }
    
    @Test
    public void shouldCloseCurrentSessionIfExceptionHappens() throws Exception {
        Message message = mock(Message.class);
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestProcessor requestProcessor = mock(OutgoingRequestProcessor.class);
        Session session = mock(Session.class);
        FutureTimer future = mock(FutureTimer.class);

        given(session.nextSequenceNumber()).willReturn(SEQ_ID);
        given(connection.newSession()).willReturn(session);
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequest.getResponseTimeoutSeconds()).willReturn(TIMEOUT_SECONDS);
        doThrow(new IOException()).when(session).sendMessage(message, 0);
        given(outgoingRequest.outgoingRequestProcessor()).willReturn(requestProcessor);
        given(outgoingRequest.isCancelled()).willReturn(false);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID)).willReturn(message);
        given(requestProcessor.isExpected(response)).willReturn(true);
        given(requester.getTimer()).willReturn(timer);
        given(timer.schedule(any(Runnable.class), eq(1L), eq(TimeUnit.SECONDS))).willReturn(future);
        given(requestProcessor.hasMoreMessages()).willReturn(false);
        
        OutgoingRequestSession requestSession = new OutgoingRequestSession(outgoingRequest, connection, requester);
        requestSession.setOutgoingRequestSessionListener(sessionListener);
        requestSession.start();
        
        // Test StartNextRequestState 
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(outgoingRequest).outgoingRequestProcessor();
        verify(connection).newSession();
        verify(session).setMessageListener(messageListnerCaptor.capture());
        verify(requestProcessor).starting();
        verify(requester, times(2)).requestTrap(handlerCaptor.capture());
        
        // Test SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(session).sendMessage(message, 0);
        verify(requester, times(3)).requestTrap(handlerCaptor.capture());
        verify(requestProcessor).forceFinished(eq(ControlStatus.FAILURE), startsWith("Unable to send message for request: "));
        
        // Test FinishProcessingRequestState
        assertIsInState(requestSession, OutgoingRequestSession.FinishProcessingRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).doFinalProcessing();
        verify(session).close();
        verify(requester, times(4)).requestTrap(handlerCaptor.capture());
        
        assertIsInState(requestSession, OutgoingRequestSession.ClosedState.class);
    }

    @Test
    public void shouldSendMessagesWhenMultipleMessageGiven() throws Exception {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestProcessor requestProcessor = mock(OutgoingRequestProcessor.class);
        Session session = mock(Session.class);
        FutureTimer future1 = mock(FutureTimer.class);
        FutureTimer future2 = mock(FutureTimer.class);
        
        given(session.nextSequenceNumber())
            .willReturn(SEQ_ID)
            .willReturn(SEQ_ID+1);
        given(connection.newSession()).willReturn(session);
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequest.getResponseTimeoutSeconds()).willReturn(TIMEOUT_SECONDS);
        given(outgoingRequest.outgoingRequestProcessor()).willReturn(requestProcessor);
        given(outgoingRequest.isCancelled()).willReturn(false);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID))
            .willReturn(message1);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID+1))
            .willReturn(message2);
        given(requestProcessor.hasMoreMessages())
            .willReturn(true)
            .willReturn(false);
        given(requestProcessor.isExpected(response)).willReturn(true);
        given(requester.getTimer()).willReturn(timer);
        given(timer.schedule(any(Runnable.class), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS)))
            .willReturn(future1)
            .willReturn(future2);
        
        OutgoingRequestSession requestSession = new OutgoingRequestSession(outgoingRequest, connection, requester);
        requestSession.setOutgoingRequestSessionListener(sessionListener);
        requestSession.start();
        
        // Test StartNextRequestState 
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(outgoingRequest).outgoingRequestProcessor();
        verify(connection).newSession();
        verify(session).setMessageListener(messageListnerCaptor.capture());
        verify(requestProcessor).starting();
        verify(requester, times(2)).requestTrap(handlerCaptor.capture());
        
        // Test SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(session).sendMessage(message1, 0);
        verify(timer).schedule(runnableCaptor.capture(), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS));
        
        // Test WaitingForResponseState
        assertIsInState(requestSession, OutgoingRequestSession.WaitingForResponseState.class);
        
        // Deliver a response message
        messageListnerCaptor.getValue().handleMessage(response);
        verify(requestProcessor).isExpected(response);
        verify(requestProcessor).hasMoreMessages();
        verify(future1).cancel();

        // Back to SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        runnableCaptor.getValue().run();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID+1);
        verify(session).sendMessage(message2, 0);
        verify(timer, times(2)).schedule(runnableCaptor.capture(), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS));
        
        // Deliver a response message
        messageListnerCaptor.getValue().handleMessage(response);
        verify(requestProcessor, times(2)).isExpected(response);
        verify(requestProcessor, times(2)).hasMoreMessages();
        verify(future2).cancel();

        // Test FinishProcessingRequestState
        assertIsInState(requestSession, OutgoingRequestSession.FinishProcessingRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).doFinalProcessing();
        verify(session).close();
        
        assertIsInState(requestSession, OutgoingRequestSession.ClosedState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(sessionListener).sessionClosed(requestSession);
    }
    
    
    @Test
    public void shouldSkipCancelledRequest() throws Exception {
        OutgoingRequest cancelledRequest = mock(OutgoingRequest.class);
        
        given(cancelledRequest.isCancelled()).willReturn(true);
        given(connection.getSiteAddress()).willReturn(siteAddress);

        OutgoingRequestSession requestSession = new OutgoingRequestSession(cancelledRequest, connection, requester);
        requestSession.start();
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        assertIsInState(requestSession, OutgoingRequestSession.ClosedState.class);
        verify(cancelledRequest).isCancelled();
        verify(connection, never()).newSession();
        verify(cancelledRequest, never()).outgoingRequestProcessor();
    }

    @Test
    public void shouldDoFullTestWhenNoErrorsOccur() throws Exception {
        Message message = mock(Message.class);
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        OutgoingRequestProcessor requestProcessor = mock(OutgoingRequestProcessor.class);
        Session session = mock(Session.class);
        FutureTimer future = mock(FutureTimer.class);
        
        given(session.nextSequenceNumber()).willReturn(SEQ_ID);
        given(connection.newSession()).willReturn(session);
        given(connection.getSiteAddress()).willReturn(siteAddress);
        given(outgoingRequest.getResponseTimeoutSeconds()).willReturn(TIMEOUT_SECONDS);
        given(outgoingRequest.outgoingRequestProcessor()).willReturn(requestProcessor);
        given(outgoingRequest.isCancelled()).willReturn(false);
        given(requestProcessor.nextMessageWithSequenceNumber(SEQ_ID)).willReturn(message);
        given(requestProcessor.isExpected(response)).willReturn(true);
        given(requester.getTimer()).willReturn(timer);
        given(timer.schedule(any(Runnable.class), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS))).willReturn(future);
        given(requestProcessor.hasMoreMessages()).willReturn(false);
        
        OutgoingRequestSession requestSession = new OutgoingRequestSession(outgoingRequest, connection, requester);
        requestSession.setOutgoingRequestSessionListener(sessionListener);
        requestSession.start();
        
        // Test StartNextRequestState 
        verify(requester).requestTrap(handlerCaptor.capture());
        assertIsInState(requestSession, OutgoingRequestSession.StartRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(outgoingRequest).outgoingRequestProcessor();
        verify(connection).newSession();
        verify(session).setMessageListener(messageListnerCaptor.capture());
        verify(requestProcessor).starting();
        verify(requester, times(2)).requestTrap(handlerCaptor.capture());
        
        // Test SendNextMessageState
        assertIsInState(requestSession, OutgoingRequestSession.SendNextMessageState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).nextMessageWithSequenceNumber(SEQ_ID);
        verify(session).sendMessage(message, 0);
        verify(timer).schedule(runnableCaptor.capture(), eq(TIMEOUT_SECONDS.longValue()), eq(TimeUnit.SECONDS));
        
        // Test WaitingForResponseState
        assertIsInState(requestSession, OutgoingRequestSession.WaitingForResponseState.class);
        // Deliver a response message
        messageListnerCaptor.getValue().handleMessage(response);
        verify(requestProcessor).isExpected(response);
        verify(future).cancel();
        
        // Test FinishProcessingRequestState
        assertIsInState(requestSession, OutgoingRequestSession.FinishProcessingRequestState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(requestProcessor).doFinalProcessing();
        verify(session).close();
        
        assertIsInState(requestSession, OutgoingRequestSession.ClosedState.class);
        handlerCaptor.getValue().handleSyncTrap();
        verify(sessionListener).sessionClosed(requestSession);
    }
    
    private void assertIsInState(OutgoingRequestSession requestSession, Class<? extends OutgoingRequestSession.State> stateClass) throws Exception {
        Field field = requestSession.getClass().getDeclaredField("currentState");
        field.setAccessible(true);
        assertEquals(stateClass, field.get(requestSession).getClass());
    }
}
