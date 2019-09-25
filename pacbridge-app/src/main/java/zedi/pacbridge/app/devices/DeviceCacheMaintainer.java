package zedi.pacbridge.app.devices;

import java.sql.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.domain.repositories.DeviceRepository;
import zedi.pacbridge.utl.IntegerSystemProperty;

public class DeviceCacheMaintainer {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCacheMaintainer.class.getName());
    
    public static final String DEVICE_CACHE_MAINTAINER_SCAN_INTERVAL_PROPERTY_NAME = "deviceCache.updateScanIntervalSeconds";
    public static final String DEVICE_CACHE_MAINTAINER_LOCK_NAME_FMT = "{0}_pacbridge$device_cache_maintainer";
    public static final String DEVICE_CACHE_MAINTAINER_THREAD_NAME = "DeviceCacheMaintainer";
    public static final Integer DEFAULT_SCAN_INTERVAL = 60;

    private static IntegerSystemProperty scanInterval = new IntegerSystemProperty(DEVICE_CACHE_MAINTAINER_SCAN_INTERVAL_PROPERTY_NAME, DEFAULT_SCAN_INTERVAL);
    
    private DeviceCache deviceCache;
    private DeviceRepository deviceRepository;
    private DeviceObjectCreator objectCreator;
    private DeviceCacheUpdateDelegateImpl updateDelegate;
    private boolean shutdown;
    private Thread thread;
    private MaintanenceRunner runner;
    private Integer scanIntervalSeconds;
    
    public DeviceCacheMaintainer() {
    }

    public DeviceCacheMaintainer(DeviceCache deviceCache, 
                                 DeviceRepository deviceRepository, 
                                 DeviceObjectCreator objectCreator, 
                                 DeviceCacheUpdateDelegateImpl updateDelegate) {
        this.deviceCache = deviceCache;
        this.objectCreator = objectCreator;
        this.deviceRepository = deviceRepository;
        this.updateDelegate = updateDelegate;
        this.scanIntervalSeconds = scanInterval.currentValue();
        this.shutdown = false;
    }
    
    @PostConstruct
    public void startup() {
        runner = new MaintanenceRunner();
        thread = new Thread(runner, DEVICE_CACHE_MAINTAINER_THREAD_NAME +"(LockWait)");
        thread.start();
    }
    
    @PreDestroy
    public void shutdown() {
        shutdown = true;
        thread.interrupt(); 
    }
    
    private void primeCache() {
        if (deviceCache.size() == 0) {
            logger.info("Performing initial load of the device cache...");
            List<Device> devices = deviceRepository.objectsFromDb(new Date(0), objectCreator);
            deviceCache.updateCacheWithDevices(devices);
            logger.info("Device cache load complete.  Total number of entries loaded: " + deviceCache.size());
        }
    }

    private void checkForUpdates() {
        updateDelegate.checkForUpdates();
    }
    
    class MaintanenceRunner implements Runnable {

        @Override
        public void run() {
            logger.info("DeviceCacheMaintainer is starting");
            primeCache();
            while (shutdown == false) {
                try {
                    Thread.sleep(scanIntervalSeconds*1000L);
                    checkForUpdates();
                } catch (InterruptedException e) {
                }
            }
            logger.info("DeviceCacheMaintainer is shutting down");
        }
    }
}
