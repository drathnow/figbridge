package zedi.pacbridge.app.devices;

import java.util.Date;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DateMatcher;

public class DeviceCacheUpdateDelegateImplTest extends BaseTestCase {

    
    @Test
    public void shouldName() throws Exception {
        
    }
//    
//    @Test
//    public void shouldSaveLatestUpdateTime() throws Exception {
//        long foo = System.currentTimeMillis();
//        Date now = new Date(foo);
//        Date then = new Date(foo + 50);
//        Date expectedUpdateTime = new Date(foo + 100L);
//        
//        Device device1 = mock(Device.class);
//        Device device2 = mock(Device.class);
//        Device device3 = mock(Device.class);    
//        
//        DeviceObjectCreator deviceObjectCreator = mock(DeviceObjectCreator.class);
//        DeviceRepository deviceRepository = mock(DeviceRepository.class);
//        DeviceCache deviceCache = mock(DeviceCache.class);
//        List<Device> deviceList = new ArrayList<>();
//        
//        deviceList.add(device1);
//        deviceList.add(device2);
//        deviceList.add(device3);
//        
//        given(device1.getLastUpdateTime()).willReturn(now);
//        given(device2.getLastUpdateTime()).willReturn(then);
//        given(device3.getLastUpdateTime()).willReturn(expectedUpdateTime);
//        
//        given(deviceCache.latestUpdateTime()).willReturn(now);
//        given(deviceRepository.objectsFromDb(argThat(matchesSqlDate(now)), eq(deviceObjectCreator))).willReturn(deviceList);
//        
//        DeviceCacheUpdateDelegateImpl delegate = new DeviceCacheUpdateDelegateImpl(deviceCache, deviceObjectCreator, deviceRepository);
//        delegate.init();
//        delegate.checkForUpdates();
//        
//        assertEquals(expectedUpdateTime, delegate.getLatestUpdateTime());
//    }
//    
//    @Test
//    public void shouldUpdateCacheIfDatabaseRowsChange() throws Exception {
//        Device device = mock(Device.class);
//        Long foo = System.currentTimeMillis();
//        Date time = new Date(foo);
//        DeviceObjectCreator deviceObjectCreator = mock(DeviceObjectCreator.class);
//        DeviceRepository deviceRepository = mock(DeviceRepository.class);
//        DeviceCache deviceCache = mock(DeviceCache.class);
//        List<Device> deviceList = new ArrayList<>();
//        
//        deviceList.add(device);
//        
//        given(device.getLastUpdateTime()).willReturn(new Date(foo-1));
//        given(deviceCache.latestUpdateTime()).willReturn(time);
//        given(deviceRepository.objectsFromDb(argThat(matchesSqlDate(time)), eq(deviceObjectCreator))).willReturn(deviceList);
//        
//        DeviceCacheUpdateDelegateImpl delegate = new DeviceCacheUpdateDelegateImpl(deviceCache, deviceObjectCreator, deviceRepository);
//        delegate.init();
//        delegate.checkForUpdates();
//        
//        assertEquals(time, delegate.getLatestUpdateTime());
//        verify(deviceCache).updateCacheWithDevices(deviceList);
//        verify(device).getLastUpdateTime();
//        verify(deviceCache).latestUpdateTime();
//        verify(deviceRepository).objectsFromDb(argThat(matchesSqlDate(time)), eq(deviceObjectCreator));
//    }

    private DateMatcher matchesSqlDate(Date date) {
        return new DateMatcher(date);
    }
}
