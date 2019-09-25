package zedi.pacbridge.app.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Stateless
public class DeviceCache {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCache.class.getName());
    
    private DeviceCacheHelper deviceCacheHelper;
    private Cache<String, Device> deviceCache;
    
    public DeviceCache() {
    }

    @Inject
    public DeviceCache(Cache<String, Device> deviceCache, DeviceCacheHelper deviceCacheHelper) {
        this.deviceCache = deviceCache;
        this.deviceCacheHelper = deviceCacheHelper;
    }

    public Device deviceForNetworkUnitId(String nuid) {
        Device device = deviceCache.get(nuid);
        if (device == null) {
            device = deviceCacheHelper.deviceForNuid(nuid);
            if (device != null)
                deviceCache.put(nuid, device);
        }
        return device;
    }
    
    public void addDevice(Device device) {
        deviceCache.put(device.getNuid(), device);
    }
    
    public Collection<Device> allCachedDevices() {
        List<Device> devices = new ArrayList<>();
        for (Device device : deviceCache.values())
            devices.add(device);
        return devices;
    }

    public boolean deleteDeviceWithNuid(String nuid) {
        logger.trace("Deleting device with NUID: " + nuid);
        return deviceCache.remove(nuid) != null;
    }

    public void updateCacheWithDevices(List<Device> changedDevices) {
        for (Device device : changedDevices) {
            logger.debug("Updating cache with new/modified device: " + device.getNuid());
            deviceCache.put(device.getNuid(), device);
        }
    }
    
    public Integer size() {
        return deviceCache.size();
    }
    
    public Date latestUpdateTime() {
        Date maxTime = new Date(0);
        Set<Entry<String, Device>> entrySet = deviceCache.entrySet();
        for (Entry<String, Device> e : entrySet)
            maxTime = e.getValue().getLastUpdateTime().after(maxTime) ? e.getValue().getLastUpdateTime() : maxTime;
        return maxTime;
    }
}