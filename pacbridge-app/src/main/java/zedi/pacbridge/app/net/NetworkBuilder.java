package zedi.pacbridge.app.net;

import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.blocks.ProtocolStackFactoryClassDiscoverer;
import zedi.pacbridge.app.config.ConfigurationException;
import zedi.pacbridge.app.config.NetworkConfig;
import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.app.config.TransportConfig;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.PropertyBag;

@Stateless
public class NetworkBuilder {
    private ProtocolStackFactoryClassDiscoverer protocolClassDiscoverer;
    private GlobalScheduledExecutor scheduledExecutor;
    private ConnectionGarbageCollector connectionGarbageCollector;
    private NetworkEventDispatcherManager dispatchManager;
    private NotificationCenter notificationCenter;
    
    public NetworkBuilder(ProtocolStackFactoryClassDiscoverer protocolClassDiscoverer, 
                          GlobalScheduledExecutor scheduledExecutor, 
                          ConnectionGarbageCollector connectionGarbageCollector,
                          NetworkEventDispatcherManager dispatchManager,
                          NotificationCenter notificationCenter) {
        this.protocolClassDiscoverer = protocolClassDiscoverer;
        this.scheduledExecutor = scheduledExecutor;
        this.connectionGarbageCollector = connectionGarbageCollector;
        this.dispatchManager = dispatchManager;
        this.notificationCenter = notificationCenter;
    }

    @Inject
    public NetworkBuilder(GlobalScheduledExecutor scheduledExecutor, 
                          ConnectionGarbageCollector connectionGarbageCollector, 
                          NetworkEventDispatcherManager dispatchManager, 
                          NotificationCenter notificationCenter) {
        this(new ProtocolStackFactoryClassDiscoverer(), scheduledExecutor, connectionGarbageCollector, dispatchManager, notificationCenter);
    }
    
    public NetworkBuilder() {
    }
    
    public Network networkForNetworkConfig(NetworkConfig networkConfig) throws ConfigurationException  {
        String typeName = networkConfig.getTypeName();
        ConnectionManager connectionManager = new IncomingOnlyConnectionManager(connectionGarbageCollector, notificationCenter, networkConfig.getInactiveTimeoutSeconds());
        PropertyBag propertyBag = new PropertyBag(networkConfig.getProperties());
        String protocolName = networkConfig.getProtocolConfig().getName();
        ConnectionBuilderFactory builderFactory = connectionBuilderFactory(typeName, propertyBag, networkConfig.getProtocolConfig(), protocolName);
        TransportConfig transportConfig = networkConfig.getTransportConfig();
        TcpListenerFactory listenerFactory = new TcpListenerFactory(transportConfig.getListeningAddress(), 
                                                                    transportConfig.getListeningPort(), 
                                                                    transportConfig.getConnectionQueueLimit(),
                                                                    dispatchManager);
        ConnectionRequestHandler requestHandler = new DefaultConnectionRequestHandler(builderFactory, networkConfig.getNetworkNumber(), connectionManager);

        return new TcpNetwork(networkConfig.getNetworkNumber(),
                              networkConfig.getTypeName(),
                               listenerFactory,
                               requestHandler,
                               connectionManager, 
                               propertyBag);
    }

    private ConnectionBuilderFactory connectionBuilderFactory(String typeName, PropertyBag propertyBag, ProtocolConfig protocolConfig, String protocolName) throws ConfigurationException {
        Class<? extends ProtocolStackFactory> protocolStackFactoryClass = protocolClassDiscoverer.protocolStackFactoryClassForProtocolName(protocolName);
        if (protocolStackFactoryClass == null)
            throw new ConfigurationException("No ProtocolStackFactoryClass found for protocol name '" + protocolName + "'");
        ConnectionBuilderFactory builderFactory = new ConnectionBuilderFactory(typeName, 
                                                                               propertyBag, 
                                                                               protocolConfig, 
                                                                               protocolStackFactoryClass,
                                                                               scheduledExecutor);
        return builderFactory;
    }
}
