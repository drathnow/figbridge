package zedi.pacbridge.app.net;

import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.net.tcp.ServerTask;


/**
 * The ServerManager is responsible for managing tasks that listen on ServerSockets for
 * TCP connection and UDP datagrams.
 */
public class ServerManager {

    public static final String TCP_SERVER_THREAD_NAME = "TCP Server";

    private ServerTask serverTask;

    ServerManager(ServerTask serverTask) {
        this.serverTask = serverTask;
    }
    
    public ServerManager() {
        this(new ServerTask());
    }

    public ListenerRegistrationAgent listenerRegistrationAgent() {
        return serverTask.getProxy();
    }

    public void startListening() {
        serverTask.getProxy().startListening();
    }
    
    public void stopListening() {
        serverTask.getProxy().stopListening();
    }

    public void start() {
        serverTask.start();
    }
    
    public void shutdown() {
        serverTask.getProxy().shutdown();
    }
}
