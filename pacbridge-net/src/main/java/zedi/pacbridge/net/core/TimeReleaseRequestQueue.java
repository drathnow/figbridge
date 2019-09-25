package zedi.pacbridge.net.core;

import java.util.PriorityQueue;

public class TimeReleaseRequestQueue<TRequest> {
    
    private PriorityQueue<RequestContainer> listenerRequestQueue;
    private SynchObject syncObject;
    
    public TimeReleaseRequestQueue() {
        this(null);
    }

    public TimeReleaseRequestQueue(SynchObject syncObject) {
        this.listenerRequestQueue = new PriorityQueue<RequestContainer>();
        this.syncObject = syncObject;
    }
    
    public void queueRequest(TRequest request, long delayMilliseconds) {
        synchronized (listenerRequestQueue) {
            RequestContainer container = new RequestContainer(request, System.currentTimeMillis()+delayMilliseconds);
            listenerRequestQueue.add(container);
        }
        if (syncObject != null)
            syncObject.notifyListener();
    }

    public TRequest nextDueRequest() {
        synchronized (listenerRequestQueue) {
            if (listenerRequestQueue.isEmpty() == false && listenerRequestQueue.peek().isDue())
                return listenerRequestQueue.poll().request;
            return null;
        }
    }

    public void cancel(TRequest request) {
        synchronized (listenerRequestQueue) {
            listenerRequestQueue.remove(request);
        }
    }
    
    public int size() {
        return listenerRequestQueue.size();
    }

    public void clear() {
        synchronized (listenerRequestQueue) {
            listenerRequestQueue.clear();
        }
    }
    
    private class RequestContainer implements Comparable<RequestContainer>{
        TRequest request;
        Long dueTime;

        RequestContainer(TRequest request, Long dueTime) {
            this.request = request;
            this.dueTime = dueTime;
        }

        boolean isDue() {
            return System.currentTimeMillis() >= dueTime;
        }

        @Override
        public int compareTo(RequestContainer otherRequest) {
            return dueTime.compareTo(otherRequest.dueTime);
        }
    }
}
