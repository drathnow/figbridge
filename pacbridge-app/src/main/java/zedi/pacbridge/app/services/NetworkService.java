package zedi.pacbridge.app.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.app.config.BridgeConfiguration;
import zedi.pacbridge.app.config.NetworkConfig;
import zedi.pacbridge.app.net.Network;
import zedi.pacbridge.app.net.NetworkBuilder;
import zedi.pacbridge.app.net.ServerManager;
import zedi.pacbridge.app.net.SiteConnector;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.PropertyBag;

@Singleton
@Startup
@EJB(name = NetworkService.JNDI_NAME, beanInterface = NetworkService.class)
@DependsOn("CacheProvider")
public class NetworkService {
    private Map<Integer, Network> networkMap;
    public static final String JNDI_NAME = "java:global/NetworkService";
    public static final String NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME = "NetworkShuttingDown";
    
    private BridgeConfiguration bridgeConfiguration;
    private ServerManager serverManager;
    private NetworkBuilder networkBuilder;
    private NotificationCenter notificationCenter;
    private NetworkEventDispatcherManager dispatcherManager;

    NetworkService(BridgeConfiguration bridgeConfiguration, 
                   ServerManager serverManager, 
                   NetworkBuilder networkBuilder, 
                   Map<Integer, Network> networkMap, 
                   NotificationCenter notificationCenter, 
                   NetworkEventDispatcherManager dispatcherManager) {
        this.bridgeConfiguration = bridgeConfiguration;
        this.serverManager = serverManager;
        this.networkBuilder = networkBuilder;
        this.networkMap = Collections.synchronizedMap(networkMap);
        this.notificationCenter = notificationCenter;
        this.dispatcherManager = dispatcherManager;
    }
    
    @Inject
    public NetworkService(BridgeConfiguration bridgeConfiguration, NetworkBuilder networkBuilder, NotificationCenter notificationCenter, NetworkEventDispatcherManager dispatcherManager) {
        this(bridgeConfiguration, new ServerManager(), networkBuilder, new TreeMap<Integer, Network>(), notificationCenter, dispatcherManager);
    }

    public NetworkService() {
    }

    public boolean isValidNetworkNumber(Integer networkNumber) {
        return networkMap.containsKey(networkNumber);
    }

    public SiteConnector siteConnectorForNetworkNumber(Integer networkNumber) {
        return networkMap.get(networkNumber);
    }
    
    public Integer maxOutgoingSessionForNetworkNumber(Integer networkNumber) {
        return networkMap.get(networkNumber).maxOutgoingRequestsSessions();
    }
    
    public PropertyBag propertyBagForNetworkNumber(Integer networkNumber) {
        return networkMap.get(networkNumber).getPropertyBag();
    }
    
    public Network networkForNetworkNumber(Integer networkNumber) {
        return networkMap.get(networkNumber);
    }
    
    public Collection<Network> getNetworks() {
        synchronized (networkMap) {
            return Collections.unmodifiableCollection(networkMap.values());
        }
    }
    
    @PostConstruct
    void start() {
        dispatcherManager.start();
        serverManager.start();
        for (NetworkConfig config : bridgeConfiguration.getNetworkConfigurations()) {
            try {
                Network network = networkBuilder.networkForNetworkConfig(config);
                networkMap.put(network.getNumber(), network);
                network.start(serverManager.listenerRegistrationAgent(), notificationCenter);
            } catch (Exception e) {
                throw new RuntimeException("Unable to start Network", e);
            }
        }
        serverManager.startListening();
    }
    
    @PreDestroy
    void shutdown() {
        notificationCenter.postNotification(NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME);
        serverManager.shutdown();
        dispatcherManager.shutdown();
    }
}
