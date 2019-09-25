package zedi.pacbridge.net.tcp;

import zedi.pacbridge.net.ReconnectionStrategy;

/**
 * A {@link ReconnectionStrategy} that does not allows reconnecting.
 */
public class NoReconnectionStrategy implements ReconnectionStrategy {

    @Override
    public boolean canAttemptToReconnect() {
        return false;
    }

    @Override
    public boolean canReconnectAfterNumberOfConnectionAttemps(int attempts) {
        return false;
    }
}
