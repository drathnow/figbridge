package zedi.pacbridge.app.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.utl.SiteAddress;

class SessionsManager {
    private final Lock lock;
    private Map<Integer, Map<SiteAddress, List<OutgoingRequestSession>>> networkToSessionsMap;

    public interface OutgoingRequestSessionInfoDelegate {
        /**
         * Invoked to collect information from the OutgoingRequestSession. Implemenation should finish their
         * processing as quickly as possible and take out no locks while collecting information from
         * the request. 
         * 
         * @param outgoingRequestSession - An OutgoingRequestSession.
         */
        public void collectInfo(OutgoingRequestSession outgoingRequestSession);
    }
    
    SessionsManager(Lock lock, Map<Integer, Map<SiteAddress,List<OutgoingRequestSession>>> networkToSessionsMap) {
        this.lock = lock;
        this.networkToSessionsMap = networkToSessionsMap;
    }
    
    SessionsManager() {
        this(new ReentrantLock(), new TreeMap<Integer, Map<SiteAddress,List<OutgoingRequestSession>>>());
    }
    
    public Integer numberOfDevicesWithSessionsForNetworkNumber(Integer networkNumber) {
        lock.lock();
        Map<SiteAddress, List<OutgoingRequestSession>> map = networkToSessionsMap.get(networkNumber);
        int size = map == null ? 0 : map.size();
        lock.unlock();
        return size;
    }
    
    public Integer numberOfSessionForSiteAddress(SiteAddress siteAddress) {
        int size = 0;
        lock.lock();
        Map<SiteAddress, List<OutgoingRequestSession>> map = networkToSessionsMap.get(siteAddress.getNetworkNumber());
        if (map != null) { 
            List<OutgoingRequestSession> sessionsList = map.get(siteAddress);
            size = sessionsList == null ? 0 : sessionsList.size();
        }
        lock.unlock();
        return size;
    }
    
    public void addOutgoingRequestSession(OutgoingRequestSession outgoingRequestSession) {
        lock.lock();
        Map<SiteAddress, List<OutgoingRequestSession>> map = networkToSessionsMap.get(outgoingRequestSession.getSiteAddress().getNetworkNumber());
        if (map == null) {
            List<OutgoingRequestSession> list = new ArrayList<OutgoingRequestSession>();
            map = new TreeMap<SiteAddress, List<OutgoingRequestSession>>();
            map.put(outgoingRequestSession.getSiteAddress(), list);
            networkToSessionsMap.put(outgoingRequestSession.getSiteAddress().getNetworkNumber(), map);
        }
        List<OutgoingRequestSession> sessionList = map.get(outgoingRequestSession.getSiteAddress());
        if (sessionList == null) {
            sessionList = new ArrayList<OutgoingRequestSession>();
            map.put(outgoingRequestSession.getSiteAddress(), sessionList);
        }
        sessionList.add(outgoingRequestSession);
        lock.unlock();
    }
    
    public void removeOutgoingRequestSession(OutgoingRequestSession outgoingRequestSession) {
        lock.lock();
        Map<SiteAddress, List<OutgoingRequestSession>> map = networkToSessionsMap.get(outgoingRequestSession.getSiteAddress().getNetworkNumber());
        if (map != null) {
            List<OutgoingRequestSession> list = map.get(outgoingRequestSession.getSiteAddress());
            if (list != null) {
                list.remove(outgoingRequestSession);
                if (list.size() == 0)
                    map.remove(outgoingRequestSession.getSiteAddress());
            }
        }
        lock.unlock();
    }
    
    public void collectInfo(OutgoingRequestSessionInfoDelegate infoDelegate) {
        lock.lock();
        for (Iterator<Map<SiteAddress, List<OutgoingRequestSession>>> mapIter = networkToSessionsMap.values().iterator(); mapIter.hasNext(); )
            for (Iterator<List<OutgoingRequestSession>> listIter = mapIter.next().values().iterator(); listIter.hasNext(); ) 
                for (Iterator<OutgoingRequestSession> iter = listIter.next().iterator(); iter.hasNext(); )
                    infoDelegate.collectInfo(iter.next());
        lock.unlock();
    }
}
