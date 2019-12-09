package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.CollectionFactory;
import zedi.pacbridge.utl.InactivityStrategy;
import zedi.pacbridge.utl.crc.CrcException;

@SuppressWarnings("rawtypes")
public class PendingMessageTrackerTest extends BaseTestCase {


    private final static int MESSAGE_ID = 42;

    @Mock
    private LinkedList<InTransitMessage> messageQueue;
    @Mock
    private Map<Integer, PendingMessage> pendingMessagesMap;
    @Mock
    private Map<Integer, ScheduledFuture<?>> pendingMessagesTimerMap;
    @Mock
    private CollectionFactory collectionFactory;
    @Mock
    private ScheduledFuture future;
    @Mock
    private FadMessageFactory messageFactory;
    @Mock
    private InactivityStrategy inactivityStrategy;
    @Mock
    private Lock lock;
    private InOrder lockOrder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(collectionFactory.newLinkedList(any(LinkedList.class))).thenReturn(messageQueue);
        when(collectionFactory.newTreeMap(any(TreeMap.class)))
            .thenReturn(pendingMessagesMap)
            .thenReturn(pendingMessagesTimerMap);
        
        lockOrder = inOrder(lock);
    }
     
    @Test
    public void shouldDiscardMessageIfCrcErrorDetected() throws Exception {
        PendingMessage pendingMessage = mock(PendingMessage.class);
        Segment segment = mock(Segment.class);
        
        InOrder inOrder = inOrder(pendingMessage);
        when(pendingMessage.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment.isLastSegment()).thenReturn(true);
        when(messageFactory.newPendingMessageWithMessageId(MESSAGE_ID)).thenReturn(pendingMessage);
        when(pendingMessage.isComplete())
            .thenReturn(true);
        doThrow(new CrcException()).when(pendingMessage).getMessage();
        
        PendingMessageTracker messageTracker = new PendingMessageTracker(inactivityStrategy, collectionFactory, lock);
        messageTracker.setMessageFactory(messageFactory);
        
        assertNull(messageTracker.payloadForSegmentMessageIfComplete(segment));
        
        inOrder.verify(pendingMessage).addSegment(segment);
        verify(pendingMessagesMap).remove(eq(MESSAGE_ID));
        verify(pendingMessagesTimerMap).remove(eq(MESSAGE_ID));
        
    }
    
    @Test
    public void shouldClear() throws Exception {
        ScheduledFuture<?> pendingFuture = mock(ScheduledFuture.class);
        List<ScheduledFuture<?>> pendingFutures = new ArrayList<ScheduledFuture<?>>();
        
        pendingFutures.add(pendingFuture);
        
        when(pendingMessagesTimerMap.values()).thenReturn(pendingFutures);
        
        PendingMessageTracker messageTracker = new PendingMessageTracker(inactivityStrategy, collectionFactory, lock);

        messageTracker.clear();
        
        verify(pendingMessagesMap).clear();
        verify(pendingFuture).cancel(eq(false));
        verify(pendingMessagesTimerMap).clear();
    }
    
    @Test
    public void shouldCreateNewPendingMessageWhenNewSegmentArrives() throws Exception {
        byte[] testBytes = new byte[]{1,2};
        PendingMessage pendingMessage = mock(PendingMessage.class);
        Segment segment1 = mock(Segment.class);
        Segment segment2 = mock(Segment.class);
        
        InOrder inOrder = inOrder(pendingMessage);
        when(pendingMessage.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment1.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment1.isLastSegment()).thenReturn(false);
        when(segment2.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment2.isLastSegment()).thenReturn(true);
        when(messageFactory.newPendingMessageWithMessageId(MESSAGE_ID)).thenReturn(pendingMessage);
        when(pendingMessage.isComplete())
            .thenReturn(false)
            .thenReturn(true);
        when(pendingMessage.getMessage()).thenReturn(testBytes);
        
        PendingMessageTracker messageTracker = new PendingMessageTracker(inactivityStrategy, collectionFactory, lock);
        messageTracker.setMessageFactory(messageFactory);
        
        assertNull(messageTracker.payloadForSegmentMessageIfComplete(segment1));
        assertEquals(testBytes, messageTracker.payloadForSegmentMessageIfComplete(segment2));
        
        inOrder.verify(pendingMessage).addSegment(segment1);
        inOrder.verify(pendingMessage).addSegment(segment2);
        verify(pendingMessagesMap).remove(eq(MESSAGE_ID));
        verify(pendingMessagesTimerMap).remove(eq(MESSAGE_ID));
    }
        
    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeletePendingMessageWhenTimerExpires() throws Exception {
        PendingMessage pendingMessage = mock(PendingMessage.class);
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        PendingTimeoutContainer container = mock(PendingTimeoutContainer.class);

        PendingMessageTracker messageTracker = new PendingMessageTracker(inactivityStrategy, collectionFactory, lock);
        messageTracker.setTimeoutContainerFactory(containerFactory);

        when(pendingMessage.getMessageId()).thenReturn(MESSAGE_ID);
        when(containerFactory.newPendingTimeoutContainer(messageTracker, MESSAGE_ID)).thenReturn(container);
        given(inactivityStrategy.scheduleInactivityRunner(container)).willReturn(future);
        when(pendingMessagesTimerMap.remove(MESSAGE_ID)).thenReturn(future);
        
        messageTracker.handleTimeoutForPendingMessageWithMessageId(MESSAGE_ID);
        lockOrder.verify(lock).lock();
        lockOrder.verify(lock).unlock();
        verify(pendingMessagesTimerMap).remove(eq(MESSAGE_ID));
        verify(pendingMessagesMap).remove(eq(MESSAGE_ID));
        verify(future).cancel(eq(false));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldTrackPendingMessage() throws Exception {
        PendingMessage pendingMessage = mock(PendingMessage.class);
        Segment segment1 = mock(Segment.class);
        
        when(segment1.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment1.isLastSegment()).thenReturn(false);
        when(messageFactory.newPendingMessageWithMessageId(MESSAGE_ID)).thenReturn(pendingMessage);
        when(pendingMessage.isComplete())
            .thenReturn(false);
        
        TimeoutContainerFactory containerFactory = mock(TimeoutContainerFactory.class);
        PendingTimeoutContainer container = mock(PendingTimeoutContainer.class);

        PendingMessageTracker messageTracker = new PendingMessageTracker(inactivityStrategy, collectionFactory, lock);
        messageTracker.setTimeoutContainerFactory(containerFactory);

        when(pendingMessage.getMessageId()).thenReturn(MESSAGE_ID);
        when(containerFactory.newPendingTimeoutContainer(messageTracker, MESSAGE_ID)).thenReturn(container);
        given(inactivityStrategy.scheduleInactivityRunner(container)).willReturn(future);
        
        assertNull(messageTracker.payloadForSegmentMessageIfComplete(segment1));
        
        lockOrder.verify(lock).lock();
        lockOrder.verify(lock).unlock();
        verify(inactivityStrategy).scheduleInactivityRunner(container);
        verify(pendingMessagesTimerMap).put(eq(MESSAGE_ID), eq(future));
    }
    
}
