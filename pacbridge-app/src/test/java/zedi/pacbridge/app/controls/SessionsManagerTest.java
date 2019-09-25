package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SessionsManagerTest extends BaseTestCase {
    private static final Integer NETWORK_NUMBER = 42;
    private static final Integer DEVICE_SESSION_COUNT = 5;
    
    private SiteAddress siteAddress = new NuidSiteAddress("foo", NETWORK_NUMBER);

    @Test
    public void whenRemovingOutgoingRequestSessionShouldDoNothingIfSiteMapDoesNotExist() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> list = mock(List.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(null);
                
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, list);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.removeOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldRemoveOutgoingRequestSessionAndRemoveSessionMapIfExistingSessionExist() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> list = mock(List.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(list);
        given(list.size()).willReturn(1);
                
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, list);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.removeOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(list).remove(requestSession);
        order.verify(siteMap, never()).remove(siteAddress);
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldRemoveOutgoingRequestSessionAndRemoveSessionMapIfNoExistingSessionExist() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> list = mock(List.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(list);
        given(list.size()).willReturn(0);
                
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, list);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.removeOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(list).remove(requestSession);
        order.verify(siteMap).remove(siteAddress);
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldCreateMapWhenSessionMapForNetworkExistsButSiteMapDoesNotExistsForSite() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> list = mock(List.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(null);
                
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, list);
        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.addOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(siteMap).put(eq(siteAddress), arg.capture());
        order.verify(lock).unlock();
        
        assertTrue(arg.getValue().contains(requestSession));
    }
    
    @Test
    public void shouldAddOutgoingRequestSessionWhenSessionMapExistsForSite() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> list = mock(List.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(list);
                
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, list);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.addOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(list).add(requestSession);
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldAddOutgoingRequestSessionWhenNoSessionMapExistsForSite() throws Exception {
        Lock lock = mock(Lock.class);
        OutgoingRequestSession requestSession = mock(OutgoingRequestSession.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        
        given(requestSession.getSiteAddress()).willReturn(siteAddress);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(null);
        
        ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
        
        InOrder order = inOrder(lock, networkToSessionsMap);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        sessionsManager.addOutgoingRequestSession(requestSession);
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(networkToSessionsMap).put(eq(NETWORK_NUMBER), arg.capture());
        order.verify(lock).unlock();
        
        Map<SiteAddress, List<OutgoingRequestSession>> map = arg.getValue();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(siteAddress));
        assertEquals(1, map.get(siteAddress).size());
        assertTrue(map.get(siteAddress).contains(requestSession));
    }
    
    @Test
    public void shouldReturnNumberOfSessionForSiteAddressIfNetworkExistButDeviceHasNoSessions() throws Exception {
        Lock lock = mock(Lock.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        List<OutgoingRequestSession> sessionList = mock(List.class);
        
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.get(siteAddress)).willReturn(sessionList);
        given(sessionList.size()).willReturn(DEVICE_SESSION_COUNT);
        
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap, sessionList);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        assertEquals(DEVICE_SESSION_COUNT.intValue(), sessionsManager.numberOfSessionForSiteAddress(siteAddress).intValue());
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).get(siteAddress);
        order.verify(sessionList).size();
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldReturnNumberOfSessionForSiteAddressIfNoNetworkExist() throws Exception {
        Lock lock = mock(Lock.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(null);
        InOrder order = inOrder(lock, networkToSessionsMap);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        assertEquals(0, sessionsManager.numberOfSessionForSiteAddress(siteAddress).intValue());
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldReturnDeviceSessionsCountPerNetworkWhenNetworkMapExists() throws Exception {
        Lock lock = mock(Lock.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        Map<SiteAddress, List<OutgoingRequestSession>> siteMap = mock(Map.class);
        
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(siteMap);
        given(siteMap.size()).willReturn(DEVICE_SESSION_COUNT);
        InOrder order = inOrder(lock, networkToSessionsMap, siteMap);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        assertEquals(DEVICE_SESSION_COUNT.intValue(), sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER).intValue());
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(siteMap).size();
        order.verify(lock).unlock();
    }
    
    @Test
    public void shouldReturnDeviceSessionsCountPerNetworkWhenNetworkMapDoesNotExists() throws Exception {
        Lock lock = mock(Lock.class);
        Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap = mock(Map.class);
        given(networkToSessionsMap.get(NETWORK_NUMBER)).willReturn(null);
        InOrder order = inOrder(lock, networkToSessionsMap);
        SessionsManager sessionsManager = new SessionsManager(lock, networkToSessionsMap);
        assertEquals(0, sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(NETWORK_NUMBER).intValue());
        order.verify(lock).lock();
        order.verify(networkToSessionsMap).get(NETWORK_NUMBER);
        order.verify(lock).unlock();
    }
}
