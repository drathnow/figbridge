package zedi.pacbridge.app.events;

import zedi.pacbridge.app.services.OutgoingRequestService;

public interface ControlEvent {
    /**
     * Invoked to allow a control event to process itself with an {@link OutgoingRequestService}.  Implemenations
     * can throw unchecked exceptions.
     * 
     * @param outgoingRequestService
     */
    public void handle(OutgoingRequestService outgoingRequestService);
}