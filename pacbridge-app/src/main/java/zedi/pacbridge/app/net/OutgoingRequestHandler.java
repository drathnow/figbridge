package zedi.pacbridge.app.net;

import zedi.pacbridge.app.controls.OutgoingRequest;

public interface OutgoingRequestHandler {
    public void handleOutgoingRequest(OutgoingRequest outgoingRequest);
}
