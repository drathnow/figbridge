package zedi.pacbridge.net;

/**
 * A <tt>SessionIdGenerator</tt> generates session numbers that uniquely identify communcation session
 * between the bridge and a remote device.  Implemenations of <tt>SessionIdGenerator</tt> must provide a 
 * threadsafe implemenation that will generate unique session IDs.
 * <br>
 * While session IDs are always returned as Integer values, the implemenation determines the actual data
 * type of the value. This the underlying communcation stack must be aware of how to use and store the value
 * into a stream.  
 * <br>
 * For example, GDN uses an unsigned, 16 bit value so it limites the range of session ID from 0 to 65534 and
 * write the value as a 16 bit short value into a packet header.   
 *
 */
public interface SessionIdGenerator {
    
    /**
     * Generates the next unique, session ID.  This method MUST be thread safe.
     * @return Integer - unique session ID.
     */
    public Integer nextSessionId();
    
    /**
     * Returns the maximum sesssion ID this generator will provide.
     * @return Integer - max session ID.
     */
    public Integer maxSessionId();
}
