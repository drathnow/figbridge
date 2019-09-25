package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import zedi.pacbridge.app.controls.ExpirationManager.DeletionEvent;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpirationManagerTest extends BaseTestCase {
    private static final Long EXP_TIME = 1L;
    private static final String REQUEST_ID = "123";
    private static final Long EVENT_ID = 3456L;
    private static final String ADDRESS = "1.2.3.4";
    
    @Mock
    private DependencyResolver dependencyResolver;
    @Mock
    private EventHandler eventPublisher;
    @Mock
    private Map<String, ScheduledFuture<?>> futureMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
    }
    
    @Override
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }
    
    @Test
    public void shouldRemoveFutureEventWhenRemoveEventFired() throws Exception {
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        CacheEntryRemovedEvent removeEvent = mock(CacheEntryRemovedEvent.class);
        Cache<String, OutgoingRequest> theCache = mock(Cache.class);
        
        given(removeEvent.isPre()).willReturn(false);
        given(removeEvent.getKey()).willReturn(REQUEST_ID);
        doReturn(future).when(futureMap).remove(REQUEST_ID);

        ExpirationManager expirationManager = new ExpirationManager(theCache, EXP_TIME, executor, futureMap);
        expirationManager.handleRemovedEvent(removeEvent);
        
        verify(removeEvent).isPre();
        verify(removeEvent).getKey();
        verify(futureMap).remove(REQUEST_ID);
        verify(future).cancel(false);
    }
    
    @Test
    public void shouldNotRemoveFutureEventWhenRemoveEventFiredAndIsPreIsTrue() throws Exception {
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        Cache<String, OutgoingRequest> theCache = mock(Cache.class);
        CacheEntryRemovedEvent removeEvent = mock(CacheEntryRemovedEvent.class);
        
        given(removeEvent.isPre()).willReturn(true);
        given(removeEvent.getKey()).willReturn(REQUEST_ID);
        
        ExpirationManager expirationManager = new ExpirationManager(theCache, EXP_TIME, executor, futureMap);
        expirationManager.handleRemovedEvent(removeEvent);
        
        verify(removeEvent).isPre();
        verify(removeEvent, never()).getKey();
        verify(futureMap, never()).remove(REQUEST_ID);
    }

    @Test
    public void shouldDeleteOutgoingRequestIfItIsInTheCache() throws Exception {
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        OutgoingRequest request = mock(OutgoingRequest.class);
        CacheContainer cacheContainer = mock(CacheContainer.class);
        Cache<String, OutgoingRequest> cache = mock(Cache.class);
        
        given(request.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(dependencyResolver.getImplementationOf(EventHandler.class)).willReturn(eventPublisher);
        doReturn(cache).when(cacheContainer).getCache();
        given(cache.remove(REQUEST_ID)).willReturn(request);
        given(request.getEventId()).willReturn(EVENT_ID);
        
        ArgumentCaptor<ZiosEventResponseEvent> arg = ArgumentCaptor.forClass(ZiosEventResponseEvent.class);

        ExpirationManager expirationManager = new ExpirationManager(cache, EXP_TIME, executor, futureMap);

        DeletionEvent event = expirationManager.new DeletionEvent(REQUEST_ID);
        
        event.run();

        verify(dependencyResolver).getImplementationOf(EventHandler.class);
        verify(cache).remove(REQUEST_ID);
        verify(eventPublisher).publishEvent(arg.capture());
        ZiosEventResponseEvent eventResponse = arg.getValue();
        assertNotNull(eventResponse);
        verify(futureMap).remove(REQUEST_ID);
    }
    
    @Test
    public void shouldNotQueueNewDeletionEventIfEventIsPre() throws Exception {
        CacheEntryCreatedEvent createEvent = mock(CacheEntryCreatedEvent.class);
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        Cache<String, OutgoingRequest> theCache = mock(Cache.class);
        DeletionEvent event = mock(DeletionEvent.class);
        
        ExpirationManager expirationManager = new ExpirationManager(theCache, EXP_TIME, executor, futureMap);
        
        given(createEvent.isPre()).willReturn(true);
        given(createEvent.getKey()).willReturn(REQUEST_ID);
        whenNew(DeletionEvent.class)
            .withArguments(REQUEST_ID)
            .thenReturn(event);
        
        expirationManager.handleCreateEvent(createEvent);
        
        verifyNew(DeletionEvent.class, never()).withArguments(REQUEST_ID);
        verify(executor, never()).schedule(event, EXP_TIME, TimeUnit.MINUTES);
    }
    
    @Test
    public void shouldDeleteRequestThatHasExpired() throws Exception {
        Cache cache = mock(Cache.class);
        OutgoingRequest request = mock(OutgoingRequest.class);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        CacheEntryCreatedEvent createEvent = mock(CacheEntryCreatedEvent.class);
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        Cache<String, OutgoingRequest> theCache = mock(Cache.class);
        DeletionEvent event = mock(DeletionEvent.class);
        
        ExpirationManager expirationManager = new ExpirationManager(theCache, EXP_TIME, executor, futureMap);
        
        given(createEvent.isPre()).willReturn(false);
        given(createEvent.getKey()).willReturn(REQUEST_ID);
        given(createEvent.getCache()).willReturn(cache);
        given(cache.get(REQUEST_ID)).willReturn(request);
        given(request.hasExpired(EXP_TIME)).willReturn(true);
        whenNew(DeletionEvent.class)
            .withArguments(REQUEST_ID)
            .thenReturn(event);
        doReturn(future).when(executor).schedule(event, EXP_TIME, TimeUnit.MINUTES);
        
        expirationManager.handleCreateEvent(createEvent);
        
        verify(cache).get(REQUEST_ID);
        verify(cache).remove(REQUEST_ID);
        verifyNew(DeletionEvent.class, never()).withArguments(REQUEST_ID);
        verify(executor, never()).schedule(event, EXP_TIME, TimeUnit.MINUTES);
        verify(futureMap, never()).put(REQUEST_ID, future);
    }
    
    
    @Test
    public void shouldQueueNewDeletionEvent() throws Exception {
        Cache cache = mock(Cache.class);
        OutgoingRequest request = mock(OutgoingRequest.class);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        CacheEntryCreatedEvent createEvent = mock(CacheEntryCreatedEvent.class);
        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        Cache<String, OutgoingRequest> theCache = mock(Cache.class);
        DeletionEvent event = mock(DeletionEvent.class);
        
        ExpirationManager expirationManager = new ExpirationManager(theCache, EXP_TIME, executor, futureMap);
        
        given(createEvent.isPre()).willReturn(false);
        given(createEvent.getKey()).willReturn(REQUEST_ID);
        given(createEvent.getCache()).willReturn(cache);
        given(cache.get(REQUEST_ID)).willReturn(request);
        given(request.hasExpired(EXP_TIME)).willReturn(false);
        whenNew(DeletionEvent.class)
            .withArguments(REQUEST_ID)
            .thenReturn(event);
        
        doReturn(future).when(executor).schedule(any(DeletionEvent.class), eq(EXP_TIME), eq(TimeUnit.MINUTES));
        
        expirationManager.handleCreateEvent(createEvent);
        
        ArgumentCaptor<DeletionEvent> arg = ArgumentCaptor.forClass(DeletionEvent.class);

        verify(executor).schedule(arg.capture(), eq(EXP_TIME), eq(TimeUnit.MINUTES));
        assertNotNull(arg.getValue());
        verify(futureMap).put(REQUEST_ID, future);
    }
}
