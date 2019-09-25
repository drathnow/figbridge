package zedi.pacbridge.app.controls;

import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;

/**
 * An <code>OutgoingRequestProcessor</code> maintains the state of a current outgoing control request. It
 * is used by an {@link OutgoingRequestSession} who's job it is to send and receive messages from a remote
 * device. 
 *
 */
public interface OutgoingRequestProcessor {
    /**
     * Indicates that processing is about to being.  Impelemnations should perform any
     * initialize functions require to start processing.
     */
    public void starting();
    
    /**
     * This method will be called repeately by the {@link OutgoingRequestSession} to
     * obtain messages that are to be sent to the remote device. The {@link OutgoingRequestSession}
     * calls this method and will then call <code>handleResponse</code> with the next message
     * received from the remote device. Implentors should return <code>null</code> when all 
     * messages have been processed.
     * <p>
     * This method must return at least one message. That is, you cannot create a {@code OutgoingRequestProcessor}
     * that contains no messages.
     * 
     * @param sequenceNumber - The sequence number to assign to the returned message.  Implemenation must assign
     * this value to the returned message.  It can be used to match up any messages returned by the remote device.
     *  
     * @return Message - next message to send
     */
    public Message nextMessageWithSequenceNumber(Integer sequenceNumber);
    
    /**
     * Each message sent to the remote device should result in a response message.  Each
     * time a response is received by an {@link OutgoingRequestSession} object it will be
     * passed back to the <code>OutgoingRequestProcessor</code> via this method.  The return
     * value determines the next step taken by the {@link OutgoingRequestSession}.  If <code>true</code>
     * is returned, the {@link OutgoingRequestSession} oject will invoke 
     * </code>nextMessage</code> and continue processing; if <code>false</code> is returned,
     * the {@link OutgoingRequestSession} will wait for the next message from the remote device
     * and pass it to this method.  
     * <p>
     * Implemenatations can use this method to receive multiple responses.
     *  
     * @return true - indicates the message was expeced. <code>false</code>
     * indicates the message was not expected;
     * 
     */
    public boolean isExpected(Message response);
    
    /**
     * Invoked to determine if this processor has more messages to send.
     *  
     * @return true of false - you figure it out.
     */
    public boolean hasMoreMessages();
    
    /**
     * Invoked if a condition arises to terminate the processing of the request.
     *  
     * @param status - Failure status
     * @param statusMessage - Message indicating the cause of the failure.
     */
    public void forceFinished(ControlStatus status, String statusMessage);
    
    /**
     * Invoked to allow the processor to complete any activities at the end of
     * processing.  This method will always be invoked, whether the processing
     * completed successfully or not.
     */
    public void doFinalProcessing();
}
