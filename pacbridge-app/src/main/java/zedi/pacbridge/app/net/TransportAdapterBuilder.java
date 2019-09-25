package zedi.pacbridge.app.net;

import zedi.pacbridge.net.TransportAdapter;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.SiteAddress;

public interface TransportAdapterBuilder {
    public TransportAdapter newTransportAdapter(SiteAddress siteAddress, DispatcherKey dispatcherKey, SocketChannelWrapper channel);
}