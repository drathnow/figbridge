package zedi.pacbridge.app.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

public class ConnectionTimeoutDelegateTest extends BaseTestCase {
    private static final Integer TIMEOUT_SECONDS = 10;
    private static final SiteAddress siteAddress = new NuidSiteAddress("Foo");
    
    @Test
    public void shouldCloseConnectionWhosLastActivityTimeIsBeyondTimeout() throws Exception {
        Long now = 100000L;
        Map<SiteAddress, Connection> map = mock(Map.class);
        SystemTime systemTime = mock(SystemTime.class);
        Connection connection1 = mock(Connection.class);
        Connection connection2 = mock(Connection.class);
        
        Collection<Connection> collection = new ArrayList<>();
        collection.add(connection1);
        collection.add(connection2);
        
        given(systemTime.getCurrentTime()).willReturn(now);
        given(map.values()).willReturn(collection);
        given(connection1.getLastActivityTime()).willReturn(95000L);
        given(connection2.getLastActivityTime()).willReturn(85000L);
        given(connection2.getSiteAddress()).willReturn(siteAddress);
        
        ConnectionTimeoutDelegate delegate = new ConnectionTimeoutDelegate(TIMEOUT_SECONDS, map, systemTime);
        delegate.run();
        
        verify(map).values();
        verify(connection1).getLastActivityTime();
        verify(connection2).getLastActivityTime();
        verify(connection1, times(0)).close();
        verify(connection2).close();
    }
}
