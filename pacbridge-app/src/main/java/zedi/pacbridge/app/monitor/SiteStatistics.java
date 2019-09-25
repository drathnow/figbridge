package zedi.pacbridge.app.monitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.zap.reporting.ZapReport;

@Indexed(index = "SiteStatisticsIndex")
public class SiteStatistics implements Serializable {
    @Field
    private String nuid;
    private int connectionCount;
    private int reportCount;
    private long readingsCount;
    private Map<String, ControlCounter> controlCounterMap;
    private int duplicateReportCount;
    
    private boolean connected;
    private String instanceName;
    
    public SiteStatistics() {
    }
    
    public SiteStatistics(String nuid) {
        this.controlCounterMap = new TreeMap<>();
        this.connectionCount = 0;
        this.nuid = nuid;
    }

    public String getSiteAddress() {
        return nuid;
    }
    
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public void incrementConnectionCount() {
        connectionCount++;
    }
    
    public void incrementReportCount(ZapReport report) {
        reportCount++;
        readingsCount += report.numberOfReadings();
    }
    
    public void incrementDuplicateReportCount() {
        duplicateReportCount++;
    }
    
    public Long getReadingsCount() {
        return readingsCount;
    }
    
    public Map<String, Integer> getControlMap() {
        Map<String, Integer> map = new HashMap<>();
        for (ControlCounter c : controlCounterMap.values())
            map.put(c.getName(), c.getCount());
        return map;
    }
    
    public void addSentControl(Control control) {
        ControlCounter controlCounter = controlCounterMap.get(control.messageType().getName());
        if (controlCounter == null) {
            controlCounter = new ControlCounter(control.messageType().getName());
            controlCounterMap.put(control.messageType().getName(), controlCounter);
        }
        controlCounter.increment();
    }
    
    public Integer getConnectionCount() {
        return connectionCount;
    }

    public Integer getReportCount() {
        return reportCount;
    }
    
    public Integer getDuplicateReportCount() {
        return duplicateReportCount;
    }
    
    private class ControlCounter implements Serializable {
        private String name;
        private int count;
        
        ControlCounter(String name) {
            this.name = name;
            this.count = 0;
        }
        
        void increment() {
            count++;
        }
        
        int getCount() {
            return count;
        }
        
        String getName() {
            return name;
        }
    }
}
