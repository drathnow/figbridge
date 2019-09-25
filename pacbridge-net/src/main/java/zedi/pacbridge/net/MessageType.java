package zedi.pacbridge.net;

public interface MessageType {
    public String getName();
    public boolean isControl();
    public Integer getNumber();
}
