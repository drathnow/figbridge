package zedi.pacbridge.app.net;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.PropertyBag;

public class ConnectionBuilderFactory {
	private String networkTypeName;
	private PropertyBag propertyBag;
    private ProtocolConfig protocolConfig;
    private Class<? extends ProtocolStackFactory> protocolStackFactoryClass;
    private GlobalScheduledExecutor scheduledExecutor;
    
    public ConnectionBuilderFactory(String networkTypeName, 
                                    PropertyBag propertyBag, 
                                    ProtocolConfig protocolConfig, 
                                    Class<? extends ProtocolStackFactory> protocolStackFactoryClass,
                                    GlobalScheduledExecutor scheduledExecutor) {
        this.networkTypeName = networkTypeName;
        this.propertyBag = propertyBag;
        this.protocolConfig = protocolConfig;
        this.protocolStackFactoryClass = protocolStackFactoryClass;
        this.scheduledExecutor = scheduledExecutor;
    }

    public ConnectionBuilder newConnectionBuilder() {
        ProtocolStackFactory protocolStackFactory;
        try {
            protocolStackFactory = protocolStackFactoryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to build ProtocolStackFactory class", e);
        }
        TcpNetworkAdapterBuilder networkAdapterBuilder = new TcpNetworkAdapterBuilder(propertyBag, protocolConfig);
        return new ConnectionBuilder(protocolStackFactory, networkAdapterBuilder, protocolConfig, propertyBag, scheduledExecutor);
    }
}
