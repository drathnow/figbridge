package zedi.figbridge.slapper.utl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import zedi.pacbridge.utl.IntegerSystemProperty;

class EventIdTracker {
    public static final String DEAD_SCANN_INTERVAL_PROPERTY_NAME = "eventIdTracker.deadScanIntervalMinutes";
    public static final Integer DEFAULT_DEAD_SCANN_INTERVAL_MINUTES = 2;
    
    private static final IntegerSystemProperty deadScanIntervalMinutes = new IntegerSystemProperty(DEAD_SCANN_INTERVAL_PROPERTY_NAME, DEFAULT_DEAD_SCANN_INTERVAL_MINUTES);
    
    private Map<String, Set<Long>> outstandingEventIdMap;
    private Map<String, Integer> missedReportCounterMap;
    private DeadIdScanner deadIdScanner;
    private Long nextScannTime;
    
    public EventIdTracker() {
        this.outstandingEventIdMap = Collections.synchronizedMap(new TreeMap<String, Set<Long>>());
        this.missedReportCounterMap = Collections.synchronizedMap(new TreeMap<String, Integer>());
        this.deadIdScanner = new DeadIdScanner();
        this.nextScannTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(deadScanIntervalMinutes.currentValue());
      
    }
    
    public void removeEventIdForDeviceName(Long eventId, String deviceName) {
        Set<Long> eventIds = outstandingEventIdMap.get(deviceName);
        if (eventIds != null)
            eventIds.remove(eventId);
    }

    public void addDevice(String deviceName) {
        outstandingEventIdMap.put(deviceName, Collections.synchronizedSet(new TreeSet<Long>()));
    }

    public void addEventIdForDeviceName(Long eventId, String deviceName) {
        if (System.currentTimeMillis() >= nextScannTime && deadIdScanner.isActive() == false) {
            new Thread(deadIdScanner).start();
            nextScannTime = System.currentTimeMillis() + DEFAULT_DEAD_SCANN_INTERVAL_MINUTES;
        }
        Set<Long> eventIds = outstandingEventIdMap.get(deviceName);
        if (eventIds != null)
            eventIds.add(eventId);
    }
    
    public Map<String, Integer> getMissedReportCounterMap() {
        return Collections.unmodifiableMap(missedReportCounterMap);
    }
    
    public long getDelinquentReports() {
        long total = 0;
        Set<String> keys;
        synchronized (outstandingEventIdMap) {
            keys = new TreeSet<>(outstandingEventIdMap.keySet());
        }
        
        for (String key : keys) {
            Set<Long> eventIds = outstandingEventIdMap.get(key);
            synchronized (eventIds) {
                if (eventIds.size() > 1)
                    total += eventIds.size() - 1;
                if (missedReportCounterMap.containsKey(key))
                    total += missedReportCounterMap.get(key);
            }
        }
        
        return total;
    }
    
    class DeadIdScanner implements Runnable {

        boolean active;
        
        public DeadIdScanner() {
            this.active = false;
        }
        
        public boolean isActive() {
            return active;
        }
        
        @Override
        public void run() {
            active = true;
            Set<String> keys;
            synchronized (outstandingEventIdMap) {
                keys = new TreeSet<>(outstandingEventIdMap.keySet());
            }
            
            for (String key : keys) {
                Set<Long> eventIds = outstandingEventIdMap.get(key);
                synchronized (eventIds) {
                    if (eventIds.size() > 10) {
                        Integer count = missedReportCounterMap.get(key);
                        if (count == null) {
                            count = 0;
                            missedReportCounterMap.put(key, count);
                        }
                        count += eventIds.size();
                        missedReportCounterMap.put(key, count);
                        eventIds.clear();
                    }
                }
            }
            active = false;
        }
    }
}
