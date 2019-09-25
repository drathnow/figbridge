package zedi.pacbridge.app.events;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.services.OutgoingRequestService;

@Stateless
@EJB(name = BridgeContext.JNDI_NAME, beanInterface = BridgeContext.class)
public class BridgeContext  {
    public static final String JNDI_NAME = "java:global/BridgeContext";
    private OutgoingRequestService outgoingRequestService;

    @Inject
    public BridgeContext(OutgoingRequestService outgoingRequestService) {
        this.outgoingRequestService = outgoingRequestService;
    }
    
    public BridgeContext() {
    }
    
    public void handle(ControlEvent controlEvent) {
        controlEvent.handle(outgoingRequestService);
    }
}
