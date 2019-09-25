package zedi.pacbridge.net.auth;

public interface DeviceCapabilities {
    public Integer version();
    public DeviceFlags deviceFlags();
    public Integer maxSessions();
    public Integer maxPacketSize();
}
