package zedi.pacbridge.net;

public interface ReconnectionStrategy {
    public boolean canAttemptToReconnect();
    public boolean canReconnectAfterNumberOfConnectionAttemps(int attempts);
}
