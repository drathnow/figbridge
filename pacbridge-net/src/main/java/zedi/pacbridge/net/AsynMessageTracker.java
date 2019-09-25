package zedi.pacbridge.net;

public interface AsynMessageTracker {
    public void messageDeliveryFailed();
    public void messageDeliverySucceeded();
}
