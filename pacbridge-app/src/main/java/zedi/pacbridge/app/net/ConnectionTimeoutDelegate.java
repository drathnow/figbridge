package zedi.pacbridge.app.net;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

class ConnectionTimeoutDelegate implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionTimeoutDelegate.class.getName());
    
    private Integer timeoutSeconds;
    private Map<SiteAddress, Connection> synchronizedConnectionMap;
    private SystemTime systemTime;
    
    ConnectionTimeoutDelegate(Integer timeoutSeconds, Map<SiteAddress, Connection> synchronizedConnectionMap, SystemTime systemTime) {
        this.timeoutSeconds = timeoutSeconds;
        this.synchronizedConnectionMap = synchronizedConnectionMap;
        this.systemTime = systemTime;
    }

    public ConnectionTimeoutDelegate(Integer timeoutSeconds, Map<SiteAddress, Connection> synchronizedConnectionMap) {
        this(timeoutSeconds, synchronizedConnectionMap, new SystemTime());
    }
    
    public void run() {
        Collection<Connection> connections;
        synchronized (synchronizedConnectionMap) {
            connections = Collections.unmodifiableCollection(synchronizedConnectionMap.values());
        }
        
        long now = systemTime.getCurrentTime();
        for (Connection connection : connections) {
            if ((now - connection.getLastActivityTime())/1000L > timeoutSeconds) {
                logger.debug("Connection " + connection.getSiteAddress().toString() + " inactivity time has exceeded threshold");
                connection.close();
            }
        }
    }
}
