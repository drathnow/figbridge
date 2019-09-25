package zedi.pacbridge.net;

/**
 * Session need to have unique ID in order to facilitate multiplexing multiple session
 * over a single connection.  The bridge will start with a max session id and decrement
 * down to half its max value before reseting to max and working down again.
 * 
 * Incoming session from the device work the oposite way.
 *
 */
public class DecrementingSessionIdGenerator implements SessionIdGenerator {
    private int currentSessionId;
    private int maxSessionId;
    private int lowerLimitSessionId;
    
    public DecrementingSessionIdGenerator(int maxSessionId) {
        this.currentSessionId = maxSessionId;
        this.maxSessionId = maxSessionId;
        this.lowerLimitSessionId = maxSessionId/2;
    }

    @Override
    public Integer nextSessionId() {
        if (currentSessionId == lowerLimitSessionId)
            currentSessionId = maxSessionId;
        return currentSessionId--;
    }

    @Override
    public Integer maxSessionId() {
        return maxSessionId;
    }
}
