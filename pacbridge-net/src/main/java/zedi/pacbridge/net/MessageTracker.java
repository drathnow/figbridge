package zedi.pacbridge.net;

import java.util.concurrent.TimeoutException;

public interface MessageTracker {
    public boolean hasBeenAcknowledged();
    public boolean hasFailed();
    public boolean hasFinished();
    public Integer messageId();
    public void waitForSuccessOrFailure(long timeoutMilliseconds) throws TimeoutException;
}