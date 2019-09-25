package zedi.pacbridge.eventgen.util;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;

public class StaticDeviceCache extends DeviceCache {

    private TreeMap<String, Device> deviceMap;
    
    public StaticDeviceCache() {
        deviceMap = new TreeMap<String, Device>();
        deviceMap.put("ZGW-DAVE", new Device("ZGW-DAVE", null, 0, new Date(0L)));
        deviceMap.put("ZED-00001", new Device("ZED-00001", null, 0, new Date(0L)));
    }
    
    @Override
    public Device deviceForNetworkUnitId(String nuid) {
        return deviceMap.get(nuid);
    }

    @Override
    public Date latestUpdateTime() {
        return null;
    }

    @Override
    public void addDevice(Device device) {
    }
    
    @Override
    public Collection<Device> allCachedDevices() {
        return null;
    }
    
    @Override
    public void updateCacheWithDevices(List<Device> changedDevices) {
    }

    @Override
    public Integer size() {
        return null;
    }

    @Override
    public boolean deleteDeviceWithNuid(String nuid) {
        return deviceMap.remove(nuid) != null;
    }

}
