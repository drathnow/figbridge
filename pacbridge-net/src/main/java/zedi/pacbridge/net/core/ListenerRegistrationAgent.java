package zedi.pacbridge.net.core;

import java.io.IOException;
import java.net.InetSocketAddress;

import zedi.pacbridge.net.ListenerStatus;

public interface ListenerRegistrationAgent {
    public ListenerStatus registerListener(InetSocketAddress listeningAddress, AcceptHandler acceptHandler, int connectionQueueLimit) throws IOException;
}
