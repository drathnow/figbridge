package zedi.pacbridge.app.net;

import zedi.pacbridge.app.controls.OutgoingRequest;

public class NoopOutgoingRequestHandler implements OutgoingRequestHandler{
    @Override
    public void handleOutgoingRequest(OutgoingRequest outgoingRequest) {
        // Do nothing
    }

}
