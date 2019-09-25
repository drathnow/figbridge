package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.ServerRequestBase;


public class ShutdownServerRequest extends ServerRequestBase implements ServerRequest {

    protected ShutdownServerRequest(RequestQueue<ServerRequest> requestQueue, ReentrantLock lock) {
        super(requestQueue, lock);
    }

    public ShutdownServerRequest(RequestQueue<ServerRequest> requestQueue) {
        this(requestQueue, new ReentrantLock());
    }

    public void handleRequestWithServer(ServerHelper server) {
        try {
            server.shutdown();
        } finally {
            signalCondition();
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ShutdownServerRequest.class.getName());
    public void shutdown() throws IOException {
        queueAndWait();
    }

}
