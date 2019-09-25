package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.ServerRequestBase;

public class StartListeningServerRequest extends ServerRequestBase implements ServerRequest {

    protected StartListeningServerRequest(RequestQueue<ServerRequest> requestQueue, ReentrantLock lock) {
        super(requestQueue, lock);
    }

    public StartListeningServerRequest(RequestQueue<ServerRequest> requestQueue) {
        this(requestQueue, new ReentrantLock());
    }

    public void handleRequestWithServer(ServerHelper server) {
        try {
            server.startListening();
        } finally {
            signalCondition();
        }
    }

    public void startListening() throws IOException {
        queueAndWait();
    }
}
