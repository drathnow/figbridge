package zedi.pacbridge.app.controls;

import java.util.Collection;

import zedi.pacbridge.utl.SiteAddress;


public interface OutgoingRequestCache extends OutgoingRequestCacheUpdateDelegate {
    public static final String OUTGOING_REQUEST_TIMEOUT_PROPERTY_NAME = "pacbridge.outgoingRequestTimeoutMinutes";
    public static final Integer DEFAULT_REQUEST_EXPIRATION_MINUTES = 180;

    public void storeOutgoingRequest(OutgoingRequest outgoingRequest);
    public boolean updateOutgoingRequest(OutgoingRequest outgoingRequest);
    public OutgoingRequest outgoingRequestForRequestId(String requestId);
    public boolean deleteOutgoingRequestWithRequestId(String requestId);
    public Collection<OutgoingRequest> outgoingRequestsForSiteAddress(SiteAddress siteAddress);
    public boolean hasOutgoingRequests(SiteAddress siteAddress);
    public Collection<OutgoingRequest> allOutgoingRequests();
}