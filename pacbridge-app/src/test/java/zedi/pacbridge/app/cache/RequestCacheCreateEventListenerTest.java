package zedi.pacbridge.app.cache;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.infinispan.Cache;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCacheCreateEventListener;
import zedi.pacbridge.app.controls.OutgoingRequestManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.SiteAddress;

public class RequestCacheCreateEventListenerTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 21;
    private static final String ADDRESS = "123";
    private static final String KEY = "12345234234";

    @Mock
    private DependencyResolver dependencyResolver;
    @Mock
    private OutgoingRequestManager requestManager;
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private Cache<String, OutgoingRequest> cache;
    @Mock
    private CacheEntryModifiedEvent<String, OutgoingRequest> modifyEvent;
    @Mock
    private OutgoingRequest outgoingRequest;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
        given(outgoingRequest.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void shouldOnlyCallOutgoingRequestManagerWhenSomethingIsReallyNew() throws Exception {
        CacheEntryCreatedEvent createEvent = mock(CacheEntryCreatedEvent.class);
        Cache cache = mock(Cache.class);
        
        given(dependencyResolver.getImplementationOf(OutgoingRequestManager.class)).willReturn(requestManager);
        given(createEvent.getKey()).willReturn(KEY);
        given(createEvent.isPre()).willReturn(true).willReturn(false);
        given(createEvent.getCache()).willReturn(cache);
        given(cache.get(KEY)).willReturn(outgoingRequest);
        
        OutgoingRequestCacheCreateEventListener listener = new OutgoingRequestCacheCreateEventListener();
        listener.handleCreateEvent(createEvent);
        listener.handleCreateEvent(createEvent);
        
        verify(cache).get(KEY);
        verify(createEvent, times(2)).isPre();
        verify(requestManager).queueOutgoingRequest(outgoingRequest);
    }
}
