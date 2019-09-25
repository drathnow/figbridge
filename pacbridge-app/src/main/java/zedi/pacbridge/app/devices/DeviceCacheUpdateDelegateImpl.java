package zedi.pacbridge.app.devices;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.domain.repositories.DeviceRepository;

@Stateful
@EJB(name = DeviceCacheUpdateDelegate.JNDI_NAME, beanInterface = DeviceCacheUpdateDelegate.class)
@DependsOn("CacheProvider")
public class DeviceCacheUpdateDelegateImpl implements DeviceCacheUpdateDelegate {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCacheUpdateDelegateImpl.class.getName());
    private DeviceObjectCreator deviceObjectCreator;
    private DeviceRepository deviceRepository;
    private DeviceCache deviceCache;
    private Date latestUpdateTime;
    
    @Inject
    public DeviceCacheUpdateDelegateImpl(DeviceCache deviceCache, DeviceObjectCreator deviceObjectCreator, DeviceRepository deviceRepository) {
        this.deviceCache = deviceCache;
        this.deviceObjectCreator = deviceObjectCreator;
        this.deviceRepository = deviceRepository;
        this.latestUpdateTime = null;
    }

    @PostConstruct
    public void init() {
        latestUpdateTime = deviceCache.latestUpdateTime();
    }
    
    @Override
    public Date getLatestUpdateTime() {
        return latestUpdateTime;
    } 

    @Override
    public void primeCache() {
        if (deviceCache.size() == 0) {
            logger.info("Performing initial load of the device cache...");
            List<Device> devices = deviceRepository.objectsFromDb(new Date(0), deviceObjectCreator);
            deviceCache.updateCacheWithDevices(devices);
            logger.info("Device cache load complete.  Total number of entries loaded: " + deviceCache.size());
        }
    }
    
    @Override
    public Date checkForUpdates() {
        try {
            List<Device> devices = deviceRepository.objectsFromDb(latestUpdateTime, deviceObjectCreator);
            if (devices.size() > 0) {
                for (Device device : devices)
                    latestUpdateTime = device.getLastUpdateTime().after(latestUpdateTime) ? device.getLastUpdateTime() : latestUpdateTime;
                deviceCache.updateCacheWithDevices(devices);
            }
        } catch (Exception e) {
            logger.error("Unable to fetch device objects", e);
        }
        return latestUpdateTime;
    }
    
}
