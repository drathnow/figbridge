package zedi.pacbridge.net.core;

import zedi.pacbridge.utl.ThreadContext;

public interface DispatcherRequest {
    public void handleRequest(DispatcherKey dispatcherKey, ThreadContext threadContext);
}
