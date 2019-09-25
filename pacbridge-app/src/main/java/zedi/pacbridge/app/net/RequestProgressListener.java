package zedi.pacbridge.app.net;

import org.json.JSONArray;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.RequestCompletionStrategy;
import zedi.pacbridge.net.controls.ControlStatus;

public interface RequestProgressListener {
    /**
     * Invoked when an outgoing request has been aborted.  This method can be used to pass additional
     * information to the implementations.
     * 
     * @param outgoingRequest - the request that has completed.
     * @param status - final status of the request
     * @param message - a message 
     * @param extraData - object with extra data.
     */
    public void requestProcessingAborted(OutgoingRequest outgoingRequest, ControlStatus status, String message, JSONArray extraData);

    /**
     * Invoked when an outgoing request has completed processing.  This signature can be used to pass additional
     * information the implementations.
     * 
     * @param outgoingRequest - the request that has completed.
     * @param completionStrategy - A RequestCompletionStrategy callback object that will be invoked to handle
     * final processing for the request
     * 
     */
    public void requestProcessingCompleted(OutgoingRequest outgoingRequest, RequestCompletionStrategy completionStrategy);

    /**
     * Invoked when an outgoing request starts processing.  This signature can be used to pass additional
     * information the implementations.
     * 
     * @param outgoingRequest - the request that has completed.
     */
    public void requestProcessingStarted(OutgoingRequest outgoingRequest);
}
