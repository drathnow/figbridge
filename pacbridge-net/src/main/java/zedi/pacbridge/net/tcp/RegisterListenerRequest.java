package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.ServerRequestBase;


public class RegisterListenerRequest extends ServerRequestBase implements ServerRequest {

    private InetSocketAddress listeningAddress;
    private AcceptHandler acceptHandler;
    private int connetionQueueLimit;
    private ListenerStatus listenerStatus;

    RegisterListenerRequest(RequestQueue<ServerRequest> requestQueue) {
        this(new ReentrantLock(), requestQueue);
    }
    
    protected RegisterListenerRequest(ReentrantLock lock, RequestQueue<ServerRequest> requestQueue) {
        super(requestQueue, lock);
    }
    
    public ListenerStatus getListenerStatus() {
        return listenerStatus;
    }
    
    public void registerListener(InetSocketAddress listeningAddress, AcceptHandler acceptHandler, int connetionQueueLimit) throws IOException {
        this.listeningAddress = listeningAddress;
        this.acceptHandler = acceptHandler;
        this.connetionQueueLimit = connetionQueueLimit;
        queueAndWait();
    }
    
    public void handleRequestWithServer(ServerHelper server) {
        try {
            listenerStatus = ((ServerHelper)server).registerListener(listeningAddress, acceptHandler, connetionQueueLimit);
        } catch (IOException e) {
            setException(e);
        } finally {
            signalCondition();
        }
    }
}
