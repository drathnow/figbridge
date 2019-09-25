package zedi.pacbridge.app.controls;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ControlRequestProgressListener.class, ZiosEventResponseEvent.class})
public class ControlRequestProgressListenerTest extends BaseTestCase {
    private static final Long EVENT_ID = 123L;
    private static final String REQUEST_ID = "123-456";
    private static final String MESSAGE = "All good";
    private static final String JSON_STRING = "{'Hello': 'World'}";
    private static final String ADDRESS = "1.2.3.4";
    
    @Mock
    private OutgoingRequestCache cache;
    @Mock
    private ControlRequest controlRequest;
    @Mock
    private EventHandler eventPublisher;
    @Mock
    private SiteAddress siteAddress;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        given(controlRequest.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
    }
    
    @Test
    public void shouldHandleAbortedSessionWithNullExtraData() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        ControlStatus status = mock(ControlStatus.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(outgoingRequest.getRequestId()).willReturn(REQUEST_ID);
        given(outgoingRequest.getEventId()).willReturn(EVENT_ID);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, MESSAGE, null)
            .thenReturn(event);

        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingAborted(outgoingRequest, status, MESSAGE, null);
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, MESSAGE, null);
        verify(eventPublisher).publishEvent(event);
        verify(cache).deleteOutgoingRequestWithRequestId(REQUEST_ID);
    }

    @Test
    public void shouldHandleAbortedSession() throws Exception {
        OutgoingRequest outgoingRequest = mock(OutgoingRequest.class);
        ControlStatus status = mock(ControlStatus.class);
        JSONArray extraData = mock(JSONArray.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(outgoingRequest.getRequestId()).willReturn(REQUEST_ID);
        given(outgoingRequest.getEventId()).willReturn(EVENT_ID);
        given(extraData.toString()).willReturn(JSON_STRING);
        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, MESSAGE, null)
            .thenReturn(event);

        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingAborted(outgoingRequest, status, MESSAGE, extraData);
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, MESSAGE, null);
        verify(eventPublisher).publishEvent(event);
        verify(cache).deleteOutgoingRequestWithRequestId(REQUEST_ID);
    }
    
    @Test
    public void shouldDeleteRequestIfControlProcessingTimesOutAndMaxRetriesAreExceeded() throws Exception {
        RequestCompletionStrategy completionStrategy = mock(RequestCompletionStrategy.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        JSONArray array = mock(JSONArray.class);
        MessageType messageType = mock(MessageType.class);
        Control control = mock(Control.class);
        
        given(completionStrategy.hasTimedOut()).willReturn(true);
        given(messageType.getName()).willReturn("Foo");
        given(control.messageType()).willReturn(messageType);
        given(controlRequest.getControl()).willReturn(control);        
        given(controlRequest.getEventId()).willReturn(EVENT_ID);
        given(controlRequest.getRequestId()).willReturn(REQUEST_ID);
        given(controlRequest.getSendAttempts()).willReturn(ControlRequestProgressListener.DEFAULT_MAX_RETRIES+2);
        given(array.toString()).willReturn(JSON_STRING);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, ControlRequestProgressListener.MAX_RETRIES_ERROR, null).thenReturn(event);

        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingCompleted(controlRequest, completionStrategy);
        
        verify(controlRequest).getSendAttempts();
        verify(cache).deleteOutgoingRequestWithRequestId(REQUEST_ID);
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Failure, ADDRESS, ControlRequestProgressListener.MAX_RETRIES_ERROR, null);
        verify(eventPublisher).publishEvent(event);
    }
    
    @Test
    public void shouldUpdateStatusToPendingIfControlProcessingTimesOutAndMaxRetriesAreNotExceeded() throws Exception {
        RequestCompletionStrategy completionStrategy = mock(RequestCompletionStrategy.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        JSONArray array = mock(JSONArray.class);
        MessageType messageType = mock(MessageType.class);
        Control control = mock(Control.class);
        
        given(completionStrategy.hasTimedOut()).willReturn(true);
        given(messageType.getName()).willReturn("Foo");
        given(control.messageType()).willReturn(messageType);
        given(controlRequest.getControl()).willReturn(control);
        given(controlRequest.getEventId()).willReturn(EVENT_ID);
        given(controlRequest.getRequestId()).willReturn(REQUEST_ID);
        given(controlRequest.getSendAttempts()).willReturn(ControlRequestProgressListener.DEFAULT_MAX_RETRIES+1);
        given(array.toString()).willReturn(JSON_STRING);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Pending, ADDRESS).thenReturn(event);

        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingCompleted(controlRequest, completionStrategy);
        
        verify(controlRequest).getSendAttempts();
        verify(controlRequest).setStatus(ControlStatus.PENDING);
        verify(cache, never()).deleteOutgoingRequestWithRequestId(REQUEST_ID);
        verify(cache).updateOutgoingRequest(controlRequest);
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Pending, ADDRESS);
        verify(eventPublisher).publishEvent(event);
    }
 
    @Test
    public void shouldUpdateContolRequestStatusAndDeleteFromCacheIfSuccessful() throws Exception {
        RequestCompletionStrategy completionStrategy = mock(RequestCompletionStrategy.class);
        JSONArray array = mock(JSONArray.class);
        MessageType messageType = mock(MessageType.class);
        Control control = mock(Control.class);
        
        given(messageType.getName()).willReturn("Foo");
        given(completionStrategy.hasTimedOut()).willReturn(false);
        given(control.messageType()).willReturn(messageType);
        given(controlRequest.getControl()).willReturn(control);
        given(controlRequest.getEventId()).willReturn(EVENT_ID);
        given(controlRequest.getRequestId()).willReturn(REQUEST_ID);
        given(array.toString()).willReturn(JSON_STRING);

        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingCompleted(controlRequest, completionStrategy);
        
        verify(cache).deleteOutgoingRequestWithRequestId(REQUEST_ID);
        verify(completionStrategy).completeProcessing();
    }
    
    @Test
    public void shouldUpdateControlRequestWhenStarting() throws Exception {
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        
        given(controlRequest.getEventId()).willReturn(EVENT_ID);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Running, ADDRESS).thenReturn(event);
        InOrder inOrder = inOrder(controlRequest, cache);
        
        ControlRequestProgressListener listener = new ControlRequestProgressListener(eventPublisher, cache);
        listener.requestProcessingStarted(controlRequest);

        inOrder.verify(controlRequest).setStatus(ControlStatus.RUNNING);
        inOrder.verify(cache).updateOutgoingRequest(controlRequest);
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Running, ADDRESS);
        verify(eventPublisher).publishEvent(event);
    }
}
