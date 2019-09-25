package zedi.pacbridge.app.net;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.ThreadContext;

public interface ConnectionRequestHandler {
    public void handleConnectionRequest(SocketChannelWrapper socketChannel, DispatcherKey dispatcherKey, ThreadContext astRequester);
}
