package zedi.pacbridge.app.net;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestManager;
import zedi.pacbridge.utl.DependencyResolver;

public class OutgoingRequestForOutgoingStrategy implements OutgoingRequestStrategy {
    @Override
    public void handleOutgoingRequest(OutgoingRequest outgoingRequest) {
        OutgoingRequestManager manager = DependencyResolver.Implementation.sharedInstance().getImplementationOf(OutgoingRequestManager.JNDI_NAME);
        manager.queueOutgoingRequest(outgoingRequest);
    }
}
