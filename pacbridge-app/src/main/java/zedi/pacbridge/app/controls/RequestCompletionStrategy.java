package zedi.pacbridge.app.controls;


/**
 * A RequestCompletionStrategy is a contract that defines the completion status of an OutgoingRequest.  
 * 
 */
public interface RequestCompletionStrategy {
    
    /**
     * Invoked to allow the RequestCompletionStrategy to complete its processing
     */
    public void completeProcessing();
    
    /**
     * Indicates if the attempt to process the outgoing request timed out and was unable to
     * complete.
     * 
     * @return true - the request timed out. false otherwise.
     */
    public boolean hasTimedOut();
    
    /**
     * Inidicates if the attempt to process the outgoing request was successful or not.
     * @return
     */
    public boolean wasSuccessful();
}
