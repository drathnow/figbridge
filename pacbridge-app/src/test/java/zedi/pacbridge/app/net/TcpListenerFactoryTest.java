package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.net.core.AcceptHandler;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TcpListenerFactory.class)
public class TcpListenerFactoryTest extends BaseTestCase {

    @Test
    public void shouldBuildAcceptHandler() throws Exception {
        NetworkEventDispatcherManager dispatcherManager = mock(NetworkEventDispatcherManager.class);
        Network network = mock(Network.class); 
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        TcpAcceptHandler handler = mock(TcpAcceptHandler.class);
        
        whenNew(TcpAcceptHandler.class).withArguments(network,  notificationCenter, dispatcherManager).thenReturn(handler);
        
        TcpListenerFactory factory = new TcpListenerFactory("1.2.3.4", 100, 10, dispatcherManager);
        AcceptHandler result = factory.getTcpAcceptHandlerForNetwork(network, notificationCenter);
        
        verifyNew(TcpAcceptHandler.class).withArguments(network,  notificationCenter, dispatcherManager);
        assertSame(handler, result);
    }
    
}
