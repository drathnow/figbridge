package zedi.pacbridge.app.devices;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.Constants;
import zedi.pacbridge.utl.IntegerSystemProperty;


public class DeviceCacheMaintainerService implements Service<String> {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCacheMaintainerService.class.getName());
    public static final ServiceName SINGLETON_SERVICE_NAME = Constants.FIGBRIDGE.append("ha", "singleton", "DeviceCacheMaintainer");
    
    public static final String DEVICE_CACHE_MAINTAINER_SCAN_INTERVAL_PROPERTY_NAME = "deviceCache.updateScanIntervalSeconds";
    public static final String DEVICE_CACHE_MAINTAINER_LOCK_NAME_FMT = "{0}_pacbridge$device_cache_maintainer";
    public static final String DEVICE_CACHE_MAINTAINER_THREAD_NAME = "DeviceCacheMaintainer";
    public static final Integer DEFAULT_SCAN_INTERVAL = 60;

    private static IntegerSystemProperty scanInterval = new IntegerSystemProperty(DEVICE_CACHE_MAINTAINER_SCAN_INTERVAL_PROPERTY_NAME, DEFAULT_SCAN_INTERVAL);
    
    private final AtomicBoolean started = new AtomicBoolean(false);
    private DeviceCacheUpdateDelegate updateDelegate;
    private boolean shutdown;
    private Thread thread;
    private MaintanenceRunner runner;
    private Integer scanIntervalSeconds;

    public DeviceCacheMaintainerService() {
        this.scanIntervalSeconds = scanInterval.currentValue();
        this.shutdown = false;
    }

    public DeviceCacheMaintainerService(DeviceCacheUpdateDelegate updateDelegate) {
        this();
        this.updateDelegate = updateDelegate;
    }
    
    public void start(StartContext context) throws StartException {
        runner = new MaintanenceRunner(updateDelegate);
        thread = new Thread(runner, DEVICE_CACHE_MAINTAINER_THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(StopContext context) {
        shutdown = true;
        thread.interrupt(); 
    }

    @Override
    public String getValue() throws IllegalStateException, IllegalArgumentException {
        logger.info(String.format("%s is %s at %s", DeviceCacheMaintainerService.class.getSimpleName(), (started.get() ? "started" : "not started"), System.getProperty("jboss.node.name")));
        return "";
    }
    
    class MaintanenceRunner implements Runnable {

        private DeviceCacheUpdateDelegate updateDelegate;

        public MaintanenceRunner(DeviceCacheUpdateDelegate updateDelegate) {
            this.updateDelegate = updateDelegate;
        }

        @Override
        public void run() {
//            try {
//                logger.info("DeviceCacheMaintainer is starting");
//                waitForDeletegate();
//                updateDelegate = InitialContext.doLookup(DeviceCacheUpdateDelegate.JNDI_NAME);
//                updateDelegate.primeCache();
//                while (shutdown == false) {
//                    logger.trace("DeviceCacheMaintainer is checking for updates");
//                    if (updateDelegate == null)
//                        updateDelegate = InitialContext.doLookup(DeviceCacheUpdateDelegate.JNDI_NAME);
//                    try {
//                        Thread.sleep(TimeUnit.SECONDS.toMillis(scanIntervalSeconds));
//                        updateDelegate.checkForUpdates();
//                    } catch (InterruptedException e) {
//                    }
//                    updateDelegate = null;
//                }
//            } catch (NamingException e) {
//                logger.error("Unable to lookup update delegate", e);
//            }
//            logger.info("DeviceCacheMaintainer is shutting down");
        }
        
        private void waitForDeletegate() {
            while (shutdown == false) {
                try {
                    InitialContext.doLookup(DeviceCacheUpdateDelegate.JNDI_NAME);
                    return;
                } catch (NamingException e) {
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e1) {
                }
                
            }
        }
    }
}
