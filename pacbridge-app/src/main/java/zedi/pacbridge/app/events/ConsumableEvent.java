package zedi.pacbridge.app.events;

public interface ConsumableEvent {
    public void handle(BridgeContext bridgeContext);
    public Long getEventId();
}
