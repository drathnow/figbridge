package zedi.pacbridge.utl.strategies;

public interface RetryStrategy {
    public boolean canRetryOperationAfterNumberOfAttempts(int attempts);
}
