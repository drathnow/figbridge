package zedi.pacbridge.app.controls;

import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.services.NetworkService;

@Stateless
public class OutgoingRequestManagerDelegateFactory {
    private NetworkService networkService;
    private RequestProgressListener requestProgressListener;
    
    @Inject
    public OutgoingRequestManagerDelegateFactory(NetworkService networkService, RequestProgressListener requestProgressListener) {
        super();
        this.networkService = networkService;
        this.requestProgressListener = requestProgressListener;
    }

    public OutgoingRequestManagerDelegateFactory() {
    }
    
    public OutgoingRequestManagerDelegate newOutgoingRequestManagerDelegate(OutgoingRequestManager outgoingRequestManager) {
        SessionsManager sessionsManager = new SessionsManager();
        return new OutgoingRequestManagerDelegate(outgoingRequestManager, sessionsManager, networkService, requestProgressListener); 
    }
}
