package zedi.pacbridge.net.auth;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.Packet;

/**
 * The <code>AuthenticationStrategy</code> implements an algorithm for authenticating connections.  Instance
 * of <code>AuthenticationStrategy</code> are used by the {@link AuthenticationMediator}.
 * <p>
 * It performs the following:
 * 
 * 1. nextMessage is called.
 * 2. handleBytesFromClient is called with bytes received from the client
 * 3. nextMessage is called.  
 * 4. if nextMessage (step 3) returns a message, that message is sent to the client.
 *      4.1  
 *  
 *
 */
public interface AuthenticationStrategy {
    
    /**
     * Called when data is received from the client.
     * 
     * @param byteBuffer - buffer containing data.  The buffer has been "flipped"
     */
    public void handleBytesFromClient(ByteBuffer byteBuffer);
    
    /**
     * Called to determine if the strategy has finished authentication processing
     * @return
     */
    public boolean isFinished();
    
    /**
     * Called to get the next packet to transmit. Any packet returned by this method must
     * be returned once, and only once.
     * @return
     */
    public Packet nextPacket();
    
    /**
     * This mehod will be invoked after {@link isFinished} is called to determine if the authentication
     * process was successful.
     * 
     * @return true - the authentication was successful, false if not.
     */
    public boolean isAuthenticated();
    
    /**
     * If {@link isAuthenticated} returns true, this will return the {@link AuthenticationContext} object
     * with the details of the authentication.
     *  
     * @return
     */
    public AuthenticationContext authenticationContext();
    
    /**
     * Resets to a known initial, unauthorized state
     */
    public void reset();
}
