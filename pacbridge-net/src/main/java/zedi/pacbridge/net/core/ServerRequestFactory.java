package zedi.pacbridge.net.core;

import zedi.pacbridge.net.tcp.ServerRequest;
import zedi.pacbridge.net.tcp.ShutdownServerRequest;
import zedi.pacbridge.net.tcp.StartListeningServerRequest;
import zedi.pacbridge.net.tcp.StopListeningServerRequest;

public class ServerRequestFactory {

    protected RequestQueue<ServerRequest> requestQueue;

    public ServerRequestFactory(RequestQueue<ServerRequest> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public StopListeningServerRequest newStopListeningRequest() {
        return new StopListeningServerRequest(requestQueue);
    }

    public StartListeningServerRequest newStartListeningRequest() {
        return new StartListeningServerRequest(requestQueue);
    }

    public ShutdownServerRequest newShutdownServerRequest() {
        return new ShutdownServerRequest(requestQueue);
    }

}