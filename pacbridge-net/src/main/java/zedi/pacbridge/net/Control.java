package zedi.pacbridge.net;


public interface Control extends Message {
    public Integer size();
    public MessageType messageType();
    public Long getEventId();
}