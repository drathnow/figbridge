package zedi.pacbridge.stp.fad;

import zedi.pacbridge.utl.strategies.RetryStrategy;


class MaxRetriesResendMessageStrategy implements ResendMessageStrategy {
    private RetryStrategy retryStrategy;
    
    public MaxRetriesResendMessageStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }
    
    @Override
    public boolean canResendMessage(InTransitMessage inTransitMessage) {
        return retryStrategy.canRetryOperationAfterNumberOfAttempts(inTransitMessage.getSendAttempts());
    }
}
