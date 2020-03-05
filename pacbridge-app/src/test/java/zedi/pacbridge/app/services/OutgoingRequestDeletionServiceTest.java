package zedi.pacbridge.app.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OutgoingRequestDeletionService.class, ZiosEventResponseEvent.class})
public class OutgoingRequestDeletionServiceTest extends BaseTestCase {
    private static final String REQUEST_ID = "Foo";
    private static final String NUID = "Bar";
    private static final Long EVENT_ID = 100L;
    
    @Mock
    private EventHandler eventPublisher;
    
    @Test
    public void shouldHandleJMSExceptionAndNotDeleteRequest() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OutgoingRequestCache cache = mock(OutgoingRequestCache.class);
        OutgoingRequest request = mock(OutgoingRequest.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);

        given(cache.outgoingRequestForRequestId(REQUEST_ID)).willReturn(request);
        given(request.getEventId()).willReturn(EVENT_ID);
        given(request.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(NUID);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Cancelled, NUID).thenReturn(event);
        doThrow(RuntimeException.class).when(eventPublisher).publishEvent(event);
        
        OutgoingRequestDeletionService service = new OutgoingRequestDeletionService(cache, eventPublisher);
        assertFalse(service.deleteOutgoingRequestWithRequestId(REQUEST_ID));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Cancelled, NUID);
        verify(eventPublisher).publishEvent(event);
        verify(cache, times(0)).deleteOutgoingRequestWithRequestId(REQUEST_ID);
    }
    
    @Test
    public void shouldReturnFalseIfOutgoingRequestDoesNotExist() throws Exception {
        OutgoingRequestCache cache = mock(OutgoingRequestCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);

        given(cache.outgoingRequestForRequestId(REQUEST_ID)).willReturn(null);
        
        InOrder order = inOrder(eventPublisher, cache);
        
        OutgoingRequestDeletionService service = new OutgoingRequestDeletionService(cache, eventPublisher);
        assertFalse(service.deleteOutgoingRequestWithRequestId(REQUEST_ID));
        
        order.verify(eventPublisher, times(0)).publishEvent(event);
        order.verify(cache, times(0)).deleteOutgoingRequestWithRequestId(REQUEST_ID);
    }
    
    @Test
    public void shouldDeleteRequestAndPublishMessage() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OutgoingRequestCache cache = mock(OutgoingRequestCache.class);
        OutgoingRequest request = mock(OutgoingRequest.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);

        given(cache.outgoingRequestForRequestId(REQUEST_ID)).willReturn(request);
        given(request.getEventId()).willReturn(EVENT_ID);
        given(request.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(NUID);
        whenNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Cancelled, NUID).thenReturn(event);
        
        InOrder order = inOrder(eventPublisher, cache);
        
        OutgoingRequestDeletionService service = new OutgoingRequestDeletionService(cache, eventPublisher);
        assertTrue(service.deleteOutgoingRequestWithRequestId(REQUEST_ID));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Cancelled, NUID);
        order.verify(eventPublisher).publishEvent(event);
        order.verify(cache).deleteOutgoingRequestWithRequestId(REQUEST_ID);
    }
}
