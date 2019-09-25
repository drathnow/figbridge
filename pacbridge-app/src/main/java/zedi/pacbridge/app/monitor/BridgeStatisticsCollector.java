package zedi.pacbridge.app.monitor;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import zedi.pacbridge.app.Constants;
import zedi.pacbridge.app.net.ConnectionRequest;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;

@Singleton
@Startup
public class BridgeStatisticsCollector implements Notifiable {
    static String BRIDGE_NAME_FMT = "{0}.{1}.{2}";

    private BridgeStatistics bridgeStatistics;
    private BridgeStatisticsCache bridgeStatisticsCache;
    
    public BridgeStatisticsCollector() {
    }
    
    @Inject
    public BridgeStatisticsCollector(BridgeStatisticsCache bridgeStatisticsCache, NotificationCenter notificationCenter) {
        this.bridgeStatisticsCache = bridgeStatisticsCache;
        String bridgeName = Constants.BRIDGE_NAME;
        bridgeStatistics = bridgeStatisticsCache.bridgeStatisticsForName(bridgeName);
        if (bridgeStatistics == null)
            bridgeStatistics = new BridgeStatistics(bridgeName);
        notificationCenter.addObserver(this, ConnectionRequest.CONNECTION_REQUEST_COMPLETED_NOTIFICATION);
    }

    @Override
    public void handleNotification(Notification notification) {
        if (ConnectionRequest.CONNECTION_REQUEST_COMPLETED_NOTIFICATION.equals(notification.getName()))
            recordConnectionHandlerTime(notification.<Long>getAttachment());
    }
    
    public void recordConnectionHandlerTime(Long handlerTime) {
        bridgeStatistics.recordConnection(handlerTime);
        bridgeStatisticsCache.updateBridgeStatistics(bridgeStatistics);
    }
}
