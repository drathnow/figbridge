package zedi.pacbridge.net.core;

import java.util.List;

class MonitorActivityAnalyzer implements Runnable {

    private List<MonitoringEvent> monitoringEvents;
    
    public MonitorActivityAnalyzer(List<MonitoringEvent> monitoringEvents, RequestQueueMonitorHelper threadPoolManager) {
        this.monitoringEvents = monitoringEvents;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Method not implemented");
    }

}
