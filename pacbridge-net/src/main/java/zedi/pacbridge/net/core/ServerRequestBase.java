package zedi.pacbridge.net.core;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.net.tcp.ServerRequest;

public abstract class ServerRequestBase implements ServerRequest {

    public static final int REQUEST_TIMEOUT_SECONDS = 10;

    private ReentrantLock lock;
    private Condition condition;
    private RequestQueue<ServerRequest> requestQueue;
    private IOException exception;

    protected ServerRequestBase(RequestQueue<ServerRequest> requestQueue, ReentrantLock lock) {
        this.requestQueue = requestQueue;
        this.lock = lock;
        this.condition = lock.newCondition();
    }
    
    protected void setException(IOException exception) {
        this.exception = exception;
    }
    
    protected void throwIfException() throws IOException {
        try {
            if (exception != null)
                throw exception;
        } finally {
            exception = null;
        }
    }

    protected void queueAndWait() throws IOException {
        lock.lock();
        try {
            setException(null);
            requestQueue.queueRequest(this);
            try {
                condition.await(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
            throwIfException();
        } finally {
            lock.unlock();
        }
    }

    protected void signalCondition() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    } 
}
