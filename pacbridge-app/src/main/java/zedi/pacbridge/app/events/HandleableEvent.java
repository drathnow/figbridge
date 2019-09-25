package zedi.pacbridge.app.events;

public interface HandleableEvent {
    public void handle(BridgeContext bridgeContext);
}
