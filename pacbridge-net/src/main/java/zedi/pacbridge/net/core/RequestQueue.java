package zedi.pacbridge.net.core;

import java.util.LinkedList;


public class RequestQueue<TRequest> {
    
    private LinkedList<TRequest> theQueue;
    private SynchObject syncObject;
    
    RequestQueue(LinkedList<TRequest> theQueue, SynchObject syncObject) {
        this.theQueue = theQueue;
        this.syncObject = syncObject;
    }

    public RequestQueue() {
        this(new LinkedList<TRequest>(), null);
    }

    public RequestQueue(SynchObject syncObject) {
        this(new LinkedList<TRequest>(), syncObject);
    }
    
    public void queueRequest(TRequest request) {
        synchronized (theQueue) {
            theQueue.addLast(request);
        }
        if (syncObject != null)
            syncObject.notifyListener();
    }
    
    public TRequest nextRequest() {
        synchronized (theQueue) {
            return theQueue.pollFirst();
        }
    }

    public int size() {
        return theQueue.size();
    }

    public void clear() {
        synchronized (theQueue) {
            theQueue.clear();
        }
    }
}
