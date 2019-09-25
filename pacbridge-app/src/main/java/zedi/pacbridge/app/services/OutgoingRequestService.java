package zedi.pacbridge.app.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.utl.SiteAddress;

@Stateless
@EJB(name = OutgoingRequestService.JNDI_NAME, beanInterface = OutgoingRequestService.class)
public class OutgoingRequestService {
    public static final String JNDI_NAME = "java:global/OutgoingRequestService";
    
    private OutgoingRequestCache outgoingRequestCache;
    
    @Inject
    public OutgoingRequestService(OutgoingRequestCache outgoingRequestCache) {
        this.outgoingRequestCache = outgoingRequestCache;
    }
    
    public OutgoingRequestService() {
    }
    
    public void queueOutgoingRequest(OutgoingRequest outgoingRequest) {
        outgoingRequestCache.storeOutgoingRequest(outgoingRequest);
    }

    public boolean hasOutgoingRequestsForSiteAddress(SiteAddress siteAddress) {
        return outgoingRequestCache.hasOutgoingRequests(siteAddress);
    }
}
