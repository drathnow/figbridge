package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.blocks.ProtocolStackFactoryClassDiscoverer;
import zedi.pacbridge.app.config.AuthenticationConfig;
import zedi.pacbridge.app.config.ConfigurationException;
import zedi.pacbridge.app.config.NetworkConfig;
import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.app.config.TransportConfig;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.PropertyBag;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NetworkBuilder.class)
public class NetworkBuilderTest extends BaseTestCase {
    private static final String PROTOCOL_NAME = "spooge";
    private static final String NETWORK_TYPE_NAME = "FOO";
    private static final Integer PORT_NUMBER = 12;
    private static final String ADDRESS = "1.2.3.4";
    private static final Integer NETWORK_NUMBER = 13;
    private static final Integer QUEUE_LIMIT = 434;
    private static final Integer TIMEOUT = 100;
    
    @Mock
    private NetworkConfig networkConfig;
    @Mock
    private TransportConfig transportConfig;
    @Mock
    private ProtocolConfig protocolConfig;
    @Mock
    private Properties properties;
    @Mock
    private AuthenticationConfig authenticationConfig;
    @Mock
    private ProtocolStackFactoryClassDiscoverer protocolStackFactoryClassDiscoverer;
    @Mock
    private IncomingOnlyConnectionManager connectionManager;
    @Mock
    private Map<String, Object> protocolProperties;
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
    @Mock
    private ConnectionGarbageCollector garbageCollector;
    @Mock
    private NetworkEventDispatcherManager dispatchManager;
    @Mock
    private NotificationCenter notificationCenter;
    
    @Test
    public void shouldTossCookiesIfProtocolStackClassNotFound() throws Exception {
        PropertyBag propertyBag = mock(PropertyBag.class);
        
        given(protocolStackFactoryClassDiscoverer.protocolStackFactoryClassForProtocolName(PROTOCOL_NAME)).willAnswer(new ClassAnswer(null));
        
        given(networkConfig.getTypeName()).willReturn(NETWORK_TYPE_NAME);
        given(networkConfig.getProperties()).willReturn(properties);
        given(networkConfig.getProtocolConfig()).willReturn(protocolConfig);
        given(networkConfig.getTransportConfig()).willReturn(transportConfig);
        given(networkConfig.getInactiveTimeoutSeconds()).willReturn(TIMEOUT);
        given(protocolConfig.getName()).willReturn(PROTOCOL_NAME);
        whenNew(IncomingOnlyConnectionManager.class).withArguments(garbageCollector, notificationCenter, TIMEOUT).thenReturn(connectionManager);
        whenNew(PropertyBag.class).withArguments(properties).thenReturn(propertyBag);
        
        NetworkBuilder networkBuilder = new NetworkBuilder(protocolStackFactoryClassDiscoverer, scheduledExecutor, garbageCollector, dispatchManager, notificationCenter);
        try {
            networkBuilder.networkForNetworkConfig(networkConfig);
            fail();
        } catch (ConfigurationException e) {
            assertTrue(e.getMessage().startsWith("No ProtocolStackFactoryClass found"));
        }
    }
    
    @Test
    public void shouldBuildDefaultNetwork() throws Exception {
        Class<? extends ProtocolStackFactory> protocolStackFactoryClass = ProtocolStackFactory.class; 
        TcpListenerFactory listenerFactory = mock(TcpListenerFactory.class);
        ConnectionBuilderFactory connectionBuilderFactory = mock(ConnectionBuilderFactory.class);
        TcpNetwork tcpNetwork = mock(TcpNetwork.class);
        PropertyBag propertyBag = mock(PropertyBag.class);
        DefaultConnectionRequestHandler requestHandler = mock(DefaultConnectionRequestHandler.class);
        
        given(protocolStackFactoryClassDiscoverer.protocolStackFactoryClassForProtocolName(PROTOCOL_NAME)).willAnswer(new ClassAnswer(protocolStackFactoryClass));
        
        given(transportConfig.getListeningAddress()).willReturn(ADDRESS);
        given(transportConfig.getListeningPort()).willReturn(PORT_NUMBER);
        given(transportConfig.getConnectionQueueLimit()).willReturn(QUEUE_LIMIT);
        given(networkConfig.isAuthenticated()).willReturn(false);
        given(networkConfig.isIncomingOnly()).willReturn(true);
        given(networkConfig.getProperties()).willReturn(properties);
        given(networkConfig.getTypeName()).willReturn(NETWORK_TYPE_NAME);
        given(networkConfig.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(networkConfig.getIdentityType()).willReturn(IdentityType.SiteProvided);
        given(networkConfig.getTransportConfig()).willReturn(transportConfig);
        given(networkConfig.getAuthenticationConfig()).willReturn(authenticationConfig);
        given(networkConfig.getProtocolConfig()).willReturn(protocolConfig);
        given(protocolConfig.getProperties()).willReturn(protocolProperties);
        given(protocolConfig.getName()).willReturn(PROTOCOL_NAME);
        
        whenNew(PropertyBag.class)
            .withArguments(properties)
            .thenReturn(propertyBag);
        
        whenNew(ConnectionBuilderFactory.class)
            .withArguments(NETWORK_TYPE_NAME, 
                    propertyBag, 
                    protocolConfig, 
                    protocolStackFactoryClass,
                    scheduledExecutor)
            .thenReturn(connectionBuilderFactory);

        whenNew(TcpListenerFactory.class)
            .withArguments(ADDRESS, PORT_NUMBER, QUEUE_LIMIT, dispatchManager)
            .thenReturn(listenerFactory);
        
        whenNew(DefaultConnectionRequestHandler.class)
            .withArguments(connectionBuilderFactory, NETWORK_NUMBER, connectionManager)
            .thenReturn(requestHandler);
        
        whenNew(IncomingOnlyConnectionManager.class).withArguments(garbageCollector, notificationCenter, 0).thenReturn(connectionManager);
        
        whenNew(TcpNetwork.class)   
            .withArguments(NETWORK_NUMBER,
                              NETWORK_TYPE_NAME,
                              listenerFactory,
                              requestHandler,
                              connectionManager, 
                              propertyBag)
            .thenReturn(tcpNetwork);
        
        NetworkBuilder networkBuilder = new NetworkBuilder(protocolStackFactoryClassDiscoverer, scheduledExecutor, garbageCollector, dispatchManager, notificationCenter);
        Network result = networkBuilder.networkForNetworkConfig(networkConfig);
        
        verifyNew(ConnectionBuilderFactory.class).withArguments(NETWORK_TYPE_NAME, 
                                                                propertyBag, 
                                                                protocolConfig, 
                                                                protocolStackFactoryClass,
                                                                scheduledExecutor);
        verifyNew(PropertyBag.class).withArguments(properties);
        verifyNew(TcpListenerFactory.class).withArguments(ADDRESS, PORT_NUMBER, QUEUE_LIMIT, dispatchManager);
        verifyNew(DefaultConnectionRequestHandler.class).withArguments(connectionBuilderFactory, NETWORK_NUMBER, connectionManager);
        verifyNew(IncomingOnlyConnectionManager.class).withArguments(garbageCollector, notificationCenter, 0);
        verifyNew(TcpNetwork.class)   
            .withArguments(NETWORK_NUMBER,
                              NETWORK_TYPE_NAME,
                              listenerFactory,
                              requestHandler,
                              connectionManager, 
                              propertyBag);
        assertSame(tcpNetwork, result);
    }

    private class ClassAnswer implements Answer<Class<?>> {
        private Class<?> clazz;
        
        public ClassAnswer(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<?> answer(InvocationOnMock invocation) throws Throwable {
            return clazz;
        }
        
    }
}
