package zedi.pacbridge.app.net;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.LoggingContext;
import zedi.pacbridge.utl.ThreadContext;

public interface RemoteDeviceConnectorListener {
    public void connected(LoggingContext loggingContext, SocketChannelWrapper socketChannel, DispatcherKey dispatcherKey, ThreadContext threadContext);
    public void connectFailed(String reason);
}
