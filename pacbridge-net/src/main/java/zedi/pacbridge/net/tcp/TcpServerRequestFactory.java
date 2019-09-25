package zedi.pacbridge.net.tcp;

import zedi.pacbridge.net.core.RequestQueue;
import zedi.pacbridge.net.core.ServerRequestFactory;

public class TcpServerRequestFactory extends ServerRequestFactory {

    public TcpServerRequestFactory(RequestQueue<ServerRequest> requestQueue) {
        super(requestQueue);
    }
    
    public RegisterListenerRequest newRegisterListenerRequest() {
        return new RegisterListenerRequest(requestQueue);
    }
}