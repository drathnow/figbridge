package zedi.pacbridge.net.tcp;

import zedi.pacbridge.net.ReconnectionStrategy;

/**
 * A {@link ReconnectionStrategy} that allows reconnecting up to a maximum number of attemps.
 */
public class ReconnectReconnectionStrategy implements ReconnectionStrategy {
    private int maxConnectionAttemps;
    
    public ReconnectReconnectionStrategy(Integer maxAttempts) {
        maxConnectionAttemps = maxAttempts;
    }
    
    @Override
    public boolean canAttemptToReconnect() {
        return true;
    }

    @Override
    public boolean canReconnectAfterNumberOfConnectionAttemps(int attempts) {
        return attempts >= maxConnectionAttemps;
    }
}
