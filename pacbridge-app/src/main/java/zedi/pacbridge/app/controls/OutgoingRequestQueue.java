package zedi.pacbridge.app.controls;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.utl.SiteAddress;

/**
 * The OutgoingRequestQueue manages the queueing of {@link OutgoingRequest} for sites that have connections
 * into the current bridge.  {@link OutgoingRequest} are maintained in two structures: one that tracks queued
 * requests per site and one that tracks the same requests in the order in which they are queued.  Clients
 * can either ask for queued requests for a specific site or can ask for the next request that was queued.  All
 * queues, whether for individual sites or global, are FIFO. 
 */
class OutgoingRequestQueue {
    private final Lock lock;
    private Deque<OutgoingRequest> requestQueue;
    private Map<SiteAddress, Deque<OutgoingRequest>> requestQueueMap;
    
    public interface OutgoingRequestInfoDelegate {
        /**
         * Invoked to collect information from the OutgoingRequest. Implemenation should finish their
         * processing as quickly as possible and take out no locks while collecting information from
         * the request. 
         * 
         * @param outgoingRequest - An OutgoingRequest.
         */
        public void collectInfo(OutgoingRequest outgoingRequest);
    }

    OutgoingRequestQueue(Lock lock, Map<SiteAddress, Deque<OutgoingRequest>> requestMap, Deque<OutgoingRequest> requestQueue) {
        this.lock = lock;
        this.requestQueueMap = requestMap;
        this.requestQueue = requestQueue;
    }
    
    OutgoingRequestQueue() {
        this(new ReentrantLock(),  new TreeMap<SiteAddress, Deque<OutgoingRequest>>(), new ArrayDeque<OutgoingRequest>(1));
    }
    
    void queueRequest(OutgoingRequest outgoingRequest) {
        lock.lock();
        Deque<OutgoingRequest> siteQueue = requestQueueMap.get(outgoingRequest.getSiteAddress());
        if (siteQueue == null) {
            siteQueue = new ArrayDeque<>(1);
            requestQueueMap.put(outgoingRequest.getSiteAddress(), siteQueue);
        }
        requestQueue.addLast(outgoingRequest);
        siteQueue.addLast(outgoingRequest); 
        lock.unlock();
    }
    
    void removeAllRequestsForSite(SiteAddress siteAddress) {
        lock.lock();
        Deque<OutgoingRequest> queue = requestQueueMap.get(siteAddress);
        if (queue != null) {
            while (queue.isEmpty() == false)
                requestQueue.remove(queue.pollFirst());
            requestQueueMap.remove(siteAddress);
        }
        lock.unlock();
    }

    OutgoingRequest nextOutgoingRequest() {
        lock.lock();
        OutgoingRequest outgoingRequest = requestQueue.pollFirst();
        if (outgoingRequest != null) {
            Deque<OutgoingRequest> queue = requestQueueMap.get(outgoingRequest.getSiteAddress());
            queue.remove(outgoingRequest);
            if (queue.size() == 0)
                requestQueueMap.remove(outgoingRequest.getSiteAddress());
        }
        lock.unlock();
        return outgoingRequest;
    }
    
    OutgoingRequest nextOutgoingRequestForSiteAddress(SiteAddress siteAddress) {
        lock.lock();
        OutgoingRequest outgoingRequest = null;
        Deque<OutgoingRequest> queue = requestQueueMap.get(siteAddress);
        if (queue != null) {
            outgoingRequest = queue.pollFirst();
            if (outgoingRequest == null || queue.size() == 0)
                requestQueueMap.remove(siteAddress);
            if (outgoingRequest != null)
                requestQueue.remove(outgoingRequest);
                
        }
        lock.unlock();
        return outgoingRequest;
    }
    
    public void collectInfo(OutgoingRequestInfoDelegate infoDelegate) {
        lock.lock();
        for (Iterator<OutgoingRequest> iter = requestQueue.iterator(); iter.hasNext(); )
            infoDelegate.collectInfo(iter.next());
        lock.unlock();
    }
}
