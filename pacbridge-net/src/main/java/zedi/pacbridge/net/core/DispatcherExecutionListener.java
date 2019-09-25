package zedi.pacbridge.net.core;

public interface DispatcherExecutionListener {
    public void dispatcherTerminated(NetworkEventDispatcher dispatcher, Throwable exception);
}
