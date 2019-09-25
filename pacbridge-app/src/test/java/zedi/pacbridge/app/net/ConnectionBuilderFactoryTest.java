package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.config.ProtocolConfig;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.PropertyBag;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConnectionBuilderFactory.class)
public class ConnectionBuilderFactoryTest extends BaseTestCase {
        
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
 
    @Test
    public void shouldBuildConnectionBuilder() throws Exception {
        String networkTypeName = "foo";
        ProtocolConfig protocolConfig = mock(ProtocolConfig.class); 
        PropertyBag propertyBag = mock(PropertyBag.class); 
        Class<? extends ProtocolStackFactory> protocolStackFactoryClass = MyProtocolStackFactory.class;
        TcpNetworkAdapterBuilder networkAdapterBuilder = mock(TcpNetworkAdapterBuilder.class);
        ConnectionBuilder connectionBuilder = mock(ConnectionBuilder.class);
     
        whenNew(TcpNetworkAdapterBuilder.class).withArguments(propertyBag, protocolConfig).thenReturn(networkAdapterBuilder);
        whenNew(ConnectionBuilder.class).withArguments(any(MyProtocolStackFactory.class), 
                                                       eq(networkAdapterBuilder), 
                                                       eq(protocolConfig), 
                                                       eq(propertyBag),
                                                       eq(scheduledExecutor)).thenReturn(connectionBuilder);
        
        ConnectionBuilderFactory factory = new ConnectionBuilderFactory(networkTypeName, propertyBag, protocolConfig, protocolStackFactoryClass, scheduledExecutor);
        ConnectionBuilder result = factory.newConnectionBuilder();

        assertSame(connectionBuilder, result);
        verifyNew(TcpNetworkAdapterBuilder.class).withArguments(propertyBag, protocolConfig);
        verifyNew(ConnectionBuilder.class).withArguments(any(MyProtocolStackFactory.class), 
                                                         eq(networkAdapterBuilder), 
                                                         eq(protocolConfig), 
                                                         eq(propertyBag),
                                                         eq(scheduledExecutor));
    }
}
