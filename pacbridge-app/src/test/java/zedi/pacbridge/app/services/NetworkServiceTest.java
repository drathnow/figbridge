package zedi.pacbridge.app.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.app.config.NetworkConfig;
import zedi.pacbridge.app.config.XmlBridgeConfiguration;
import zedi.pacbridge.app.net.Network;
import zedi.pacbridge.app.net.NetworkBuilder;
import zedi.pacbridge.app.net.ServerManager;
import zedi.pacbridge.net.core.ListenerRegistrationAgent;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;

public class NetworkServiceTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 1;
    private static final Integer MAX_SESSIONS = 21;
    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";
    
    @Mock
    private NetworkEventDispatcherManager dispatchManager;
    @Mock
    private XmlBridgeConfiguration bridgeConfiguration;
    @Mock
    private ServerManager serverManager;
    @Mock
    private NetworkBuilder networkBuilder;
    @Mock
    private NetworkConfig networkConfig1;
    @Mock
    private NetworkConfig networkConfig2;
    @Mock
    private Network network1;
    @Mock
    private Network network2;
    @Mock
    private ListenerRegistrationAgent agent;
    @Mock
    private Map<Integer, Network> networkMap;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(networkConfig1.getTypeName()).willReturn(NAME1);
        given(networkConfig2.getTypeName()).willReturn(NAME2);
    }
            
    @Test
    public void shouldTellMeIfNetworkNumberIsValid() throws Exception {
        given(networkMap.containsKey(NETWORK_NUMBER)).willReturn(true);
        given(networkMap.containsKey(99)).willReturn(false);
        NetworkService networkService = new NetworkService(bridgeConfiguration, serverManager, networkBuilder, networkMap, null, dispatchManager);
        assertTrue(networkService.isValidNetworkNumber(NETWORK_NUMBER));
        assertFalse(networkService.isValidNetworkNumber(99));
    }
    
    @Test
    public void shouldReturnMaxOutgoingSessionForNetworkNumber() throws Exception {
        given(networkMap.get(NETWORK_NUMBER)).willReturn(network1);
        given(network1.maxOutgoingRequestsSessions()).willReturn(MAX_SESSIONS);
        NetworkService networkService = new NetworkService(bridgeConfiguration, serverManager, networkBuilder, networkMap, null, dispatchManager);
        assertEquals(MAX_SESSIONS, networkService.maxOutgoingSessionForNetworkNumber(NETWORK_NUMBER));
        verify(network1).maxOutgoingRequestsSessions();
    }
    
    @Test
    public void shouldStopListeningWhenShutdown() throws Exception {
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        
        NetworkService networkService = new NetworkService(bridgeConfiguration, serverManager, networkBuilder, networkMap, notificationCenter, dispatchManager);

        InOrder inOrder = inOrder(serverManager, dispatchManager, notificationCenter);

        networkService.shutdown();
        
        inOrder.verify(notificationCenter).postNotification(NetworkService.NETWORK_SHUTTING_DOWN_NOTIFICATION_NAME);
        inOrder.verify(serverManager).shutdown();
        inOrder.verify(dispatchManager).shutdown();
    }
    
    @Test
    public void shouldStartNetworks() throws Exception {
        List<NetworkConfig> configs = new ArrayList<NetworkConfig>(Arrays.asList(networkConfig1, networkConfig2));
        
        given(bridgeConfiguration.getNetworkConfigurations()).willReturn(configs);
        given(serverManager.listenerRegistrationAgent()).willReturn(agent);
        given(networkBuilder.networkForNetworkConfig(networkConfig1)).willReturn(network1);
        given(networkBuilder.networkForNetworkConfig(networkConfig2)).willReturn(network2);

        InOrder inOrder = inOrder(dispatchManager, bridgeConfiguration, networkBuilder, network1, network2, serverManager);
        
        NetworkService networkService = new NetworkService(bridgeConfiguration, serverManager, networkBuilder, networkMap, null, dispatchManager);
        
        networkService.start();
        
        inOrder.verify(dispatchManager).start();
        inOrder.verify(serverManager).start();
        inOrder.verify(bridgeConfiguration).getNetworkConfigurations();
        inOrder.verify(networkBuilder).networkForNetworkConfig(networkConfig1);
        inOrder.verify(network1).start(agent, null);
        inOrder.verify(networkBuilder).networkForNetworkConfig(networkConfig2);
        inOrder.verify(network2).start(agent, null);
        inOrder.verify(serverManager).startListening();
    }

}
