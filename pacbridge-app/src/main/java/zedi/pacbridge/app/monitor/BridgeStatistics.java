package zedi.pacbridge.app.monitor;

import org.hibernate.search.annotations.Field;

public class BridgeStatistics {

    @Field
    private String name;
    private ConnectionsPerMinuteTracker connectionsPerMinuteTracker; 
    private ConnectionHandlerTimeTracker connectionHandlerTimeTracker;
    private PublishingTimeTracker publishingTimeTracker;
        
    public BridgeStatistics(String name) {
        this.name = name;
        this.connectionHandlerTimeTracker = new ConnectionHandlerTimeTracker();
        this.connectionsPerMinuteTracker = new ConnectionsPerMinuteTracker();
        this.publishingTimeTracker = new PublishingTimeTracker();
    }
    
    public String getName() {
        return name;
    }
    
    public void recordConnection(Long handlerTime) {        
        connectionsPerMinuteTracker.incrementConnectCount();
        connectionHandlerTimeTracker.addConnectionHandlerTime(handlerTime);
    }
    
    public void recordPublishingTime(long timeInMilliseconds) {
        publishingTimeTracker.addPublishingTime(timeInMilliseconds);
    }
}
