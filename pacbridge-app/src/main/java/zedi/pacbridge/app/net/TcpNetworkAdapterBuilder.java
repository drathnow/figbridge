package zedi.pacbridge.app.net;

import java.io.IOException;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.TcpNetworkAdapter;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;

public class TcpNetworkAdapterBuilder implements NetworkAdapterBuilder {
    private PropertyBag propertyBag;
    private ProtocolConfig protocolConfig;
    
    public TcpNetworkAdapterBuilder(PropertyBag propertyBag, ProtocolConfig protocolConfig) {
        this.propertyBag = propertyBag;
        this.protocolConfig = protocolConfig;
    }

    @Override
    public NetworkAdapter newNetworkAdapter(SiteAddress siteAddress, DispatcherKey dispatcherKey, SocketChannelWrapper channel) throws IOException {
        return new TcpNetworkAdapter(siteAddress, channel, dispatcherKey, TraceLogger.L1);
    }
}