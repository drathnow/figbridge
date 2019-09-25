package zedi.pacbridge.app.net;

import zedi.pacbridge.app.controls.OutgoingRequest;

public interface OutgoingRequestStrategy {
    public void handleOutgoingRequest(OutgoingRequest outgoingRequest);    
}
