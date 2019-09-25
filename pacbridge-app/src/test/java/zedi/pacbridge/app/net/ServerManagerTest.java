package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.tcp.ServerProxy;
import zedi.pacbridge.net.tcp.ServerTask;
import zedi.pacbridge.test.BaseTestCase;

public class ServerManagerTest extends BaseTestCase {
    @Mock
    private ServerTask serverTask;
    @Mock
    private ServerProxy proxy;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(serverTask.getProxy()).willReturn(proxy);
    }
    
    @Test
    public void shouldShutdown() throws Exception {
        ServerManager serverManager = new ServerManager(serverTask);
        serverManager.shutdown();
        verify(serverTask).getProxy();
        verify(proxy).shutdown();
    }
    
    @Test
    public void shouldStart() throws Exception {
        ServerManager serverManager = new ServerManager(serverTask);
        serverManager.start();
        verify(serverTask).start();
    }
    
    @Test
    public void shouldStopListening() throws Exception {
        ServerManager serverManager = new ServerManager(serverTask);
        
        serverManager.stopListening();
        verify(serverTask).getProxy();
        verify(proxy).stopListening();
    }
    
    @Test
    public void shouldStartListener() throws Exception {
        ServerManager serverManager = new ServerManager(serverTask);
        serverManager.startListening();
        verify(serverTask).getProxy();
        verify(proxy).startListening();
    }
    
    @Test
    public void shouldStartProxy() throws Exception {
        ServerManager serverManager = new ServerManager(serverTask);
        assertSame(proxy, serverManager.listenerRegistrationAgent());
        verify(serverTask).getProxy();
    }
}
