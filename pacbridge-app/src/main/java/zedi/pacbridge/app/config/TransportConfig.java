package zedi.pacbridge.app.config;

import zedi.pacbridge.net.TransportType;

public interface TransportConfig {
    public TransportType getTransportType();
    public boolean isIncomingOnly();
    public String getListeningAddress();
    public Integer getListeningPort();
    public Integer getRemotePort();
    public Integer getConnectionQueueLimit();
}
