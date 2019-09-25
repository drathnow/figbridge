package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

@SuppressWarnings({"unchecked", "rawtypes"})
public class OutgoingRequestQueueTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 42;

    private SiteAddress siteAddress = new NuidSiteAddress("foo", NETWORK_NUMBER);
    
    @Test
    public void whenAskedForNextOutgoingRequestAndThereIsNoneShouldReturnNull() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(request.getSiteAddress()).willReturn(siteAddress);
        given(requestQueue.pollFirst()).willReturn(null);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        assertNull(queue.nextOutgoingRequest());

        inOrder.verify(lock).lock();
        inOrder.verify(requestQueue).pollFirst();
        inOrder.verify(lock).unlock();

        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
        
    }
    
    @Test
    public void whenNextRequestCalledShouldRemoveFromBothGlobalQueueAndSiteQueue() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> siteQueue = mock(Deque.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(request.getSiteAddress()).willReturn(siteAddress);
        given(requestQueue.pollFirst()).willReturn(request);
        given(requestMap.get(siteAddress)).willReturn(siteQueue);
        given(siteQueue.pollFirst()).willReturn(request);
        given(siteQueue.size()).willReturn(0);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request, siteQueue);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        assertSame(request, queue.nextOutgoingRequest());

        inOrder.verify(lock).lock();
        inOrder.verify(requestQueue).pollFirst();
        inOrder.verify(request).getSiteAddress();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(siteQueue).remove(request);
        inOrder.verify(siteQueue).size();
        inOrder.verify(request).getSiteAddress();
        inOrder.verify(requestMap).remove(siteAddress);
        inOrder.verify(lock).unlock();

        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
    }
    
    @Test
    public void whenNextOutgoingRequestForSiteAddressShouldRemoveRequestFromBothQueuesAndRemoveMapForSiteIfNoMoreRequestsAreQueued() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> siteQueue = mock(Deque.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(requestMap.get(siteAddress)).willReturn(siteQueue);
        given(siteQueue.pollFirst()).willReturn(request);
        given(siteQueue.size()).willReturn(0);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request, siteQueue);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        assertSame(request, queue.nextOutgoingRequestForSiteAddress(siteAddress));

        inOrder.verify(lock).lock();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(siteQueue).pollFirst();
        inOrder.verify(siteQueue).size();
        inOrder.verify(requestMap).remove(siteAddress);
        inOrder.verify(requestQueue).remove(request);
        inOrder.verify(lock).unlock();

        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
    }

    @Test
    public void whenNextOutgoingRequestForSiteAddressShouldRemoveRequestFromBothQueuesButNotDeleteFromMapIfMoreRequestsAreQueued() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> siteQueue = mock(Deque.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(requestMap.get(siteAddress)).willReturn(siteQueue);
        given(siteQueue.pollFirst()).willReturn(request);
        given(siteQueue.size()).willReturn(1);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request, siteQueue);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        assertSame(request, queue.nextOutgoingRequestForSiteAddress(siteAddress));

        inOrder.verify(lock).lock();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(siteQueue).pollFirst();
        inOrder.verify(siteQueue).size();
        inOrder.verify(requestQueue).remove(request);
        inOrder.verify(lock).unlock();

        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
    }
    
    @Test
    public void whenRemovingAllRequestsForASiteShouldRemoveFromBothQueues() throws Exception {
        OutgoingRequest request1 = mock(OutgoingRequest.class);
        OutgoingRequest request2 = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> siteQueue = mock(Deque.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(siteQueue.isEmpty())
            .willReturn(false)
            .willReturn(false)
            .willReturn(true);
        given(siteQueue.pollFirst())
            .willReturn(request1)
            .willReturn(request2);
        given(requestMap.get(siteAddress)).willReturn(siteQueue);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request1, siteQueue);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        queue.removeAllRequestsForSite(siteAddress);
        
        inOrder.verify(lock).lock();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(siteQueue).isEmpty();
        inOrder.verify(siteQueue).pollFirst();
        inOrder.verify(requestQueue).remove(request1);
        inOrder.verify(siteQueue).isEmpty();
        inOrder.verify(siteQueue).pollFirst();
        inOrder.verify(requestQueue).remove(request2);
        inOrder.verify(requestMap).remove(siteAddress);
        inOrder.verify(lock).unlock();
        
        verifyNoMoreInteractions(request1, lock, requestMap, requestQueue);
    }
    
    @Test
    public void whenAddingAQueueRequestShouldAddToExistingSiteQueueAndGlobalQueue() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> siteQueue = mock(Deque.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(request.getSiteAddress()).willReturn(siteAddress);
        given(requestMap.get(siteAddress)).willReturn(siteQueue);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request, siteQueue);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        queue.queueRequest(request);
        
        inOrder.verify(lock).lock();
        inOrder.verify(request).getSiteAddress();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(requestQueue).addLast(request);
        inOrder.verify(siteQueue).addLast(request);
        inOrder.verify(lock).unlock();
        
        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
    }
    
    @Test
    public void whenAddingAQueuedRequestShouldStoreInBothSiteMapAndOverallQueue() throws Exception {
        OutgoingRequest request = mock(OutgoingRequest.class);
        Lock lock = mock(Lock.class);
        Map<SiteAddress, Deque<OutgoingRequest>> requestMap = mock(Map.class);
        Deque<OutgoingRequest> requestQueue = mock(Deque.class);

        given(request.getSiteAddress()).willReturn(siteAddress);
        given(requestMap.get(siteAddress)).willReturn(null);
        
        InOrder inOrder = inOrder(lock, requestMap, requestQueue, request);
        ArgumentCaptor<Deque> arg = ArgumentCaptor.forClass(Deque.class);

        OutgoingRequestQueue queue = new OutgoingRequestQueue(lock, requestMap, requestQueue);
        queue.queueRequest(request);
        
        inOrder.verify(lock).lock();
        inOrder.verify(request).getSiteAddress();
        inOrder.verify(requestMap).get(siteAddress);
        inOrder.verify(request).getSiteAddress();
        inOrder.verify(requestMap).put(eq(siteAddress), arg.capture());
        inOrder.verify(requestQueue).addLast(request);
        inOrder.verify(lock).unlock();
        
        assertTrue(arg.getValue().contains(request));
        
        verifyNoMoreInteractions(request, lock, requestMap, requestQueue);
    }
}
