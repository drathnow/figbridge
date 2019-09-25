package zedi.pacbridge.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ListenerStatus;
import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.net.core.ServerRequestFactory;

public class ServerProxy implements ListenerRegistrationAgent {
    private static Logger logger = LoggerFactory.getLogger(ServerProxy.class);

    protected ServerRequestFactory requestFactory;

    public ServerProxy(ServerRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void shutdown() {
        ShutdownServerRequest request = requestFactory.newShutdownServerRequest();
        try {
            request.shutdown();
        } catch (IOException e) {
            logger.error("Unexpected IOException during shutdown", e);
        }
    }

    public void startListening() {
        StartListeningServerRequest request = requestFactory.newStartListeningRequest();
        try {
            request.startListening();
        } catch (IOException e) {
            logger.error("Unexpected IOException during shutdown", e);
        }
    }

    public void stopListening() {
        StopListeningServerRequest request = requestFactory.newStopListeningRequest();
        try {
            request.stopListening();
        } catch (IOException e) {
            logger.error("Unexpected IOException while stopping listener", e);
        }
    }
    
    public ListenerStatus registerListener(InetSocketAddress listeningAddress, AcceptHandler acceptHandler, int connectionQueueLimit) throws IOException {
        RegisterListenerRequest request = ((TcpServerRequestFactory)requestFactory).newRegisterListenerRequest();
        request.registerListener(listeningAddress, acceptHandler, connectionQueueLimit);
        return request.getListenerStatus();
    }
}
