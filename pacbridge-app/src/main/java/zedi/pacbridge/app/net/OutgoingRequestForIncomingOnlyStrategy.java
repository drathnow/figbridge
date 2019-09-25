package zedi.pacbridge.app.net;

import zedi.pacbridge.app.controls.OutgoingRequest;

public class OutgoingRequestForIncomingOnlyStrategy implements OutgoingRequestStrategy {

    @Override
    public void handleOutgoingRequest(OutgoingRequest outgoingRequest) {
        // Do nothing
    }

}
