package zedi.pacbridge.utl.strategies;


public class DefaultRetryStrategy implements RetryStrategy {
    private int maxNumberOfRetries;
    
    public DefaultRetryStrategy(int maxNumberOfRetries) {
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    @Override
    public boolean canRetryOperationAfterNumberOfAttempts(int attemps) {
        return attemps < maxNumberOfRetries;
    }
}
