package zedi.pacbridge.app.monitor;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.net.Report;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SiteAddress;

public class DuplicateReportTracker {
    public static final String DUPLICATE_REPORT_EXPIRY_TIME_SECONDS_PROPERTY_NAME = "duplicatReport.expiryTimeSeconds";
    public static final long DEFAULT_COUNTER_EXPIRE_TIME_MINUTES = 7200;

    static IntegerSystemProperty expiryTime = new IntegerSystemProperty(DUPLICATE_REPORT_EXPIRY_TIME_SECONDS_PROPERTY_NAME, DEFAULT_COUNTER_EXPIRE_TIME_MINUTES);
    
    final Lock lock = new ReentrantLock();
    SiteAddress address;
    Map<String, HashData> hashDataList = new TreeMap<String, HashData>();
    long nextCleanupTime = 0;

    public DuplicateReportTracker(SiteAddress siteAddress) {
        this.address = siteAddress;
    }

    public boolean isDuplicateReport(Report report) {
        lock.lock();
        try {
            removeExpiredCountersIfRequired();
            HashData hashData = hashDataList.get(report.uniqueId());
            if (hashData == null) {
                hashData = new HashData(report.uniqueId());
                hashDataList.put(report.uniqueId(), hashData);
                return false;
            } else {
                hashData.incrementDuplicateCounter();
                return true;
            }

        } finally {
            lock.unlock();
        }
    }

    public int getDuplicateReportCount() {
        lock.lock();
        try {
            int total = 0;
            for (HashData duplicateCounter : hashDataList.values())
                total += duplicateCounter.duplicateCount;
            return total;
        } finally {
            lock.unlock();
        }
    }

    private void removeExpiredCountersIfRequired() {
        if (System.currentTimeMillis() > nextCleanupTime) {
            nextCleanupTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
            for (Iterator<HashData> iterator = hashDataList.values().iterator(); iterator.hasNext();) {
                HashData counter = iterator.next();
                if (counter.hasExpired())
                    iterator.remove();
            }
        }
    }

    class HashData implements Comparable<String> {

        Date lastUpdate;
        int duplicateCount;
        private String md5Hash;

        public HashData(String md5Hash) {
            this.lastUpdate = new Date();
            this.md5Hash = md5Hash;
        }

        public boolean hasExpired() {
            return (System.currentTimeMillis() - lastUpdate.getTime()) > (expiryTime.currentValue()* 1000L);
        }

        public boolean hasDuplicates() {
            return duplicateCount > 0;
        }

        public void incrementDuplicateCounter() {
            lastUpdate = new Date();
            duplicateCount++;
        }

        @Override
        public int compareTo(String hashData) {
            return md5Hash.compareTo(hashData);
        }
    }
}