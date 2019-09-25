package zedi.pacbridge.app.net;

import java.io.IOException;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;

public class ConnectionBuilder {
    private ProtocolStackFactory protocolStackfactory;
    private NetworkAdapterBuilder networkAdapterBuilder;
    private ProtocolConfig protocolConfig;
    private PropertyBag propertyBag;
    private GlobalScheduledExecutor scheduledExecutor;
    
    public ConnectionBuilder(ProtocolStackFactory protocolStackfactory, 
                             NetworkAdapterBuilder networkAdapterBuilder, 
                             ProtocolConfig protocolConfig, 
                             PropertyBag propertyBag,
                             GlobalScheduledExecutor scheduledExecutor) {
        this.protocolStackfactory = protocolStackfactory;
        this.networkAdapterBuilder = networkAdapterBuilder;
        this.protocolConfig = protocolConfig;
        this.propertyBag = propertyBag;
        this.scheduledExecutor = scheduledExecutor;
    }

    public Connection newConnection(SiteAddress siteAddress, SocketChannelWrapper channelWrapper, DispatcherKey dispatcherKey, ThreadContext astRequester) throws IOException {
        NetworkAdapter networkAdapter = networkAdapterBuilder.newNetworkAdapter(siteAddress, dispatcherKey, channelWrapper);
        ProtocolStack protocolStack = protocolStackfactory.newProtocolStack(protocolConfig, siteAddress, astRequester, networkAdapter, propertyBag);
        return new DeviceConnection(siteAddress, astRequester, protocolStack, scheduledExecutor);
    }
}
