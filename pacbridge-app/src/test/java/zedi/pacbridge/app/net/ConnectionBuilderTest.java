package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConnectionBuilder.class)
public class ConnectionBuilderTest extends BaseTestCase {
    @Mock
    private ProtocolStackFactory protocolStackfactory;
    @Mock
    private NetworkAdapterBuilder networkAdapterBuilder;
    @Mock
    private ProtocolConfig protocolConfig;
    @Mock
    private PropertyBag propertyBag;
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;

    @Test
    public void shouldBuildConnectionBuilder() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        SocketChannelWrapper channelWrapper = mock(SocketChannelWrapper.class);
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        ThreadContext astRequester = mock(ThreadContext.class);
        DeviceConnection connection = mock(DeviceConnection.class);
        NetworkAdapter adapter = mock(NetworkAdapter.class);
        ProtocolStack protocolStack = mock(ProtocolStack.class);
        
        given(networkAdapterBuilder.newNetworkAdapter(siteAddress, dispatcherKey, channelWrapper)).willReturn(adapter);
        given(protocolStackfactory.newProtocolStack(protocolConfig, siteAddress, astRequester, adapter, propertyBag)).willReturn(protocolStack);
        
        whenNew(DeviceConnection.class)
            .withArguments(siteAddress, astRequester, protocolStack, scheduledExecutor)
            .thenReturn(connection);
        
        
        ConnectionBuilder builder = new ConnectionBuilder(protocolStackfactory, networkAdapterBuilder, protocolConfig, propertyBag, scheduledExecutor);
        Connection result = builder.newConnection(siteAddress, channelWrapper, dispatcherKey, astRequester);
        assertSame(connection, result);
        
        verify(networkAdapterBuilder).newNetworkAdapter(siteAddress, dispatcherKey, channelWrapper);
        verify(protocolStackfactory).newProtocolStack(protocolConfig, siteAddress, astRequester, adapter, propertyBag);
        verifyNew(DeviceConnection.class).withArguments(siteAddress, astRequester, protocolStack, scheduledExecutor);
    }
}
