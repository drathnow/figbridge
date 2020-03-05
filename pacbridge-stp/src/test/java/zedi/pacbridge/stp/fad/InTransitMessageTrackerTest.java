package zedi.pacbridge.stp.fad;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.CollectionFactory;
import zedi.pacbridge.utl.InactivityStrategy;

@SuppressWarnings("unchecked")
public class InTransitMessageTrackerTest extends BaseTestCase {

    private final static int MAX_RETRIES = 2;
    private final static int MESSAGE_ID = 42;

    @Mock
    private MessageWindow messageWindow;
    @Mock
    private LinkedList<InTransitMessage> messageQueue;
    @Mock
    private Map<Integer, ScheduledFuture<?>> inTransitMessagesTimerMap;
    @Mock
    private CollectionFactory collectionFactory;
    @SuppressWarnings("rawtypes")
    @Mock
    private ScheduledFuture future;
    @Mock
    private InactivityStrategy inactivityStrategy;
    @Mock
    private ResendMessageStrategy resendMessageStrategy;
    @Mock
    private FadMessageHandler messageSender;
    @Mock
    private RetransmitEventHandler retransmitEventHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        given(collectionFactory.newLinkedList(any(LinkedList.class))).willReturn(messageQueue);
        given(collectionFactory.newTreeMap(any(TreeMap.class)))
           .willReturn(inTransitMessagesTimerMap);
    }
    
    @Test
    public void shouldReset() throws Exception {
        ScheduledFuture<?> inTransitFuture = mock(ScheduledFuture.class);
        List<ScheduledFuture<?>> inTransitFutures = new ArrayList<ScheduledFuture<?>>();
        
        inTransitFutures.add(inTransitFuture);
        
        given(inTransitMessagesTimerMap.values()).willReturn(inTransitFutures);
        
        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);

        messageTracker.reset();
        
        verify(messageWindow).clear();
        verify(inTransitFuture).cancel(eq(false));
        verify(inTransitMessagesTimerMap).clear();
    }
        
    @Test
    public void shouldNotResendMessageWhenMaxRetriesExceeded() throws Exception {
        ResendSegmentRequest resendRequest = mock(ResendSegmentRequest.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);
        FadMessageHandler messageSender = mock(FadMessageHandler.class);
        
        given(resendMessageStrategy.canResendMessage(inTransitMessage)).willReturn(false);
        given(resendRequest.getMessageId()).willReturn(MESSAGE_ID);
        given(messageWindow.inTransitMessageForMessageId(MESSAGE_ID)).willReturn(inTransitMessage);
        given(resendRequest.isResendSegmentRequest()).willReturn(false);
        given(inTransitMessage.getSendAttempts()).willReturn(MAX_RETRIES);
        given(inTransitMessagesTimerMap.remove(MESSAGE_ID)).willReturn(future);
        given(resendRequest.isResendRequest()).willReturn(true);
        given(resendRequest.isAcknowledgement()).willReturn(false);

        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        
        messageTracker.handleControlMessage(resendRequest, messageSender);
        
        verify(messageSender, never()).handleMessage(inTransitMessage);
        verify(messageWindow).stopTrackingMessageWithId(eq(MESSAGE_ID));
        verify(future).cancel(eq(false));
        verify(inTransitMessagesTimerMap).remove(eq(MESSAGE_ID));
        verify(inTransitMessage).setMessageStatus(FadMessageStatus.FAILED);
    }
    
    @Test
    public void shouldHandleAckForInTransitMessage() throws Exception {
        FadMessageHandler messageSender = mock(FadMessageHandler.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);
        AckMessage ackMessage = mock(AckMessage.class);

        given(messageWindow.inTransitMessageForMessageId(MESSAGE_ID)).willReturn(inTransitMessage);
        given(ackMessage.getMessageId()).willReturn(MESSAGE_ID);
        given(inTransitMessagesTimerMap.remove(MESSAGE_ID)).willReturn(future);
        given(inTransitMessage.hasBeenAcknowledged()).willReturn(true);
        given(messageQueue.isEmpty()).willReturn(true);
        given(messageWindow.hasRoom()).willReturn(false);
        given(ackMessage.isResendRequest()).willReturn(false);
        given(ackMessage.isAcknowledgement()).willReturn(true);
        
        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        
        messageTracker.handleControlMessage(ackMessage, messageSender);
        
        verify(messageWindow).stopTrackingMessageWithId(MESSAGE_ID);
        verify(inTransitMessage).setMessageStatus(FadMessageStatus.ACKNOWLEDGED);
        verify(future).cancel(eq(false));
        verify(messageQueue).isEmpty();
        verify(messageWindow, never()).hasRoom();
    }
    
    @Test
    public void shouldSendNextMessageInQueueWhenInTransitMessageExceedsMaxRetries() throws Exception {
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        InTransitMessage inTransitMessage1 = mock(InTransitMessage.class);
        InTransitMessage inTransitMessage2 = mock(InTransitMessage.class);
        RetransmitRunner container = mock(RetransmitRunner.class);

        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        messageTracker.setTimeoutContainerFactory(containerFactory);
        
        given(containerFactory.retransmitContainerForMessage(retransmitEventHandler, MESSAGE_ID)).willReturn(container);
        given(inTransitMessage2.getMessageId()).willReturn(MESSAGE_ID);
        given(messageWindow.inTransitMessageForMessageId(MESSAGE_ID))
            .willReturn(inTransitMessage1);
        given(messageWindow.hasRoom()).willReturn(true);
        given(inTransitMessage1.getSendAttempts()).willReturn(MAX_RETRIES+1);
        given(inTransitMessagesTimerMap.remove(MESSAGE_ID)).willReturn(future);
        given(messageQueue.isEmpty()).willReturn(false);
        given(messageQueue.removeFirst()).willReturn(inTransitMessage2);

        messageTracker.handleResendRequestForMessageWithMessageId(MESSAGE_ID, messageSender);

        verify(messageSender, never()).handleMessage(inTransitMessage1);
        verify(messageWindow).stopTrackingMessageWithId(eq(MESSAGE_ID));
        verify(inTransitMessage1).setMessageStatus(FadMessageStatus.FAILED);
        verify(inTransitMessage2).setMessageStatus(FadMessageStatus.INTRANSIT);
        verify(inTransitMessagesTimerMap).remove(eq(MESSAGE_ID));
        verify(future).cancel(eq(false));
        
        verify(inactivityStrategy).scheduleInactivityRunner(container);
        verify(messageWindow).trackMessageAndAssignMessageId(eq(inTransitMessage2));
        verify(messageSender).handleMessage(inTransitMessage2);
    }
        
    @Test
    public void shouldNotResendMessageThatHasExceededMaxRetries() throws Exception {
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);

        
        given(resendMessageStrategy.canResendMessage(inTransitMessage)).willReturn(false);
        
        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        messageTracker.setTimeoutContainerFactory(containerFactory);

        given(messageWindow.inTransitMessageForMessageId(MESSAGE_ID))
            .willReturn(inTransitMessage);
        given(inTransitMessage.getSendAttempts()).willReturn(MAX_RETRIES+1);
        given(inTransitMessagesTimerMap.remove(MESSAGE_ID)).willReturn(future);

        messageTracker.handleResendRequestForMessageWithMessageId(MESSAGE_ID, messageSender);

        verify(inactivityStrategy, never()).scheduleInactivityRunner(any(Runnable.class));
        verify(messageSender, never()).handleMessage(inTransitMessage);
        verify(messageWindow).stopTrackingMessageWithId(eq(MESSAGE_ID));
        verify(inTransitMessagesTimerMap).remove(eq(MESSAGE_ID));
        verify(future).cancel(eq(false));
    }
    
    @Test
    public void shouldHandleTimeoutForInTransitMessage() throws Exception {
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);
        RetransmitRunner container = mock(RetransmitRunner.class);

        
        given(resendMessageStrategy.canResendMessage(inTransitMessage)).willReturn(true);
        given(inTransitMessage.getMessageId()).willReturn(MESSAGE_ID).willReturn(MESSAGE_ID);
        given(messageWindow.inTransitMessageForMessageId(MESSAGE_ID)).willReturn(inTransitMessage);
        given(inTransitMessage.getSendAttempts()).willReturn(1);
        
        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, new CollectionFactory(), retransmitEventHandler);
        messageTracker.setTimeoutContainerFactory(containerFactory);
        
        given(containerFactory.retransmitContainerForMessage(retransmitEventHandler, MESSAGE_ID)).willReturn(container);

        messageTracker.handleResendRequestForMessageWithMessageId(MESSAGE_ID, messageSender);
        
        verify(messageSender).handleMessage(inTransitMessage);

        verify(inactivityStrategy).scheduleInactivityRunner(container);
        verify(messageWindow).inTransitMessageForMessageId(eq(MESSAGE_ID));
    }

    @Test
    public void shouldQueueMessageIfNoRoomInMessageWindow() throws Exception {
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);

        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        messageTracker.setTimeoutContainerFactory(containerFactory);
        
        given(messageWindow.hasRoom()).willReturn(false);

        messageTracker.sendAndTrackInTransitMessage(inTransitMessage, messageSender);

        verify(messageQueue).addLast(eq(inTransitMessage));
        verify(inactivityStrategy, never()).scheduleInactivityRunner(any(Runnable.class));
        verify(messageWindow, never()).trackMessageAndAssignMessageId(eq(inTransitMessage));
        verify(messageSender, never()).handleMessage(inTransitMessage);
    }
        
    @Test
    public void shouldTrackInTransitMessage() throws Exception {
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);
        RetransmitRunner container = mock(RetransmitRunner.class);

        InTransitMessageTracker messageTracker = new InTransitMessageTracker(inactivityStrategy, resendMessageStrategy, messageWindow, collectionFactory, retransmitEventHandler);
        messageTracker.setTimeoutContainerFactory(containerFactory);

        given(messageWindow.hasRoom()).willReturn(true);
        given(inTransitMessage.getMessageId()).willReturn(MESSAGE_ID);
        given(containerFactory.retransmitContainerForMessage(retransmitEventHandler, MESSAGE_ID)).willReturn(container);

        messageTracker.sendAndTrackInTransitMessage(inTransitMessage, messageSender);

        verify(inactivityStrategy).scheduleInactivityRunner(container);
        verify(messageWindow).trackMessageAndAssignMessageId(eq(inTransitMessage));
        verify(messageSender).handleMessage(inTransitMessage);
    }
    
}
