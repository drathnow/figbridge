package zedi.pacbridge.app.controls;


public interface OutgoingRequestCacheUpdateDelegate {
    
    /**
     * Updates an entry in the cache. It is possible that the update will fail if the 
     * entry has been deleted from the cache.
     * 
     * @param outgoingRequest
     * @return true if the entry exists and was updated, false if it was not found in the cache.
     */
    public boolean updateOutgoingRequest(OutgoingRequest outgoingRequest);
    public void storeOutgoingRequest(OutgoingRequest outgoingRequest);
    public boolean deleteOutgoingRequestWithRequestId(String requestId);
}
