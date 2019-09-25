package zedi.pacbridge.net.core;

import java.io.IOException;

public class NetworkEventDispatcherFactory {

    public NetworkEventDispatcher newNetworkEventDispatcher(RequestQueue<DispatcherRequest> requestQueue, EventWorkerThreadPool eventWorkerThreadPool) throws IOException {
        return new NetworkEventDispatcher(requestQueue);
    }
}
