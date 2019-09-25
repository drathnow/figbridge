package zedi.pacbridge.app.net;

import java.io.IOException;

import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.SiteAddress;

public interface NetworkAdapterBuilder {
    public NetworkAdapter newNetworkAdapter(SiteAddress siteAddress, DispatcherKey dispatcherKey, SocketChannelWrapper channel) throws IOException;
}
