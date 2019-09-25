package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.ServerRequestBase;

public class StopListeningServerRequest extends ServerRequestBase implements ServerRequest {

    public StopListeningServerRequest(RequestQueue<ServerRequest> requestQueue) {
        this(requestQueue, new ReentrantLock());
    }
    
    protected StopListeningServerRequest(RequestQueue<ServerRequest> requestQueue, ReentrantLock lock) {
        super(requestQueue, lock);
    }

    public void stopListening() throws IOException {
        queueAndWait();
    }
    
    @Override
    public void handleRequestWithServer(ServerHelper server) {
        try {
            server.stopListening();
        } finally {
            signalCondition();
        }
    }

}
