package zedi.pacbridge.net;

public interface PacketHeader {
    public Integer getSessionId();
    public boolean containsUnsolicitedMessage();
    public HeaderType headerType();
    public MessageType messageType();
}
