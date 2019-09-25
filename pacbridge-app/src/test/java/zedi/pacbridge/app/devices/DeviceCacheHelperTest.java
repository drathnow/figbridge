package zedi.pacbridge.app.devices;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DeviceCacheHelperTest extends BaseTestCase {
    private static final String NUID = "123";

    @Test
    public void shouldName() throws Exception {
        
    }
    
//    @Test
//    public void shouldDealWithAnyException() throws Exception {
//        RuntimeException exception = mock(RuntimeException.class);
//        DeviceObjectCreator deviceObjectCreator = mock(DeviceObjectCreator.class);
//        DeviceRepository deviceRepository = mock(DeviceRepository.class);
//
//        given(deviceRepository.objectWithSerialNumber(NUID, deviceObjectCreator)).willThrow(exception);
//
//        DeviceCacheHelper helper = new DeviceCacheHelper(deviceObjectCreator, deviceRepository);
//        assertNull(helper.deviceForNuid(NUID));
//    }
//
//    @Test
//    public void shouldDealWithMoreThanOneException() throws Exception {
//        MoreThanOneException exception = mock(MoreThanOneException.class);
//        DeviceObjectCreator deviceObjectCreator = mock(DeviceObjectCreator.class);
//        DeviceRepository deviceRepository = mock(DeviceRepository.class);
//
//        given(deviceRepository.objectWithSerialNumber(NUID, deviceObjectCreator)).willThrow(exception);
//
//        DeviceCacheHelper helper = new DeviceCacheHelper(deviceObjectCreator, deviceRepository);
//        assertNull(helper.deviceForNuid(NUID));
//    }
//    
//    @Test
//    public void shouldReturnDeviceFromRepo() throws Exception {
//        Device expectedDevice = mock(Device.class);
//        DeviceObjectCreator deviceObjectCreator = mock(DeviceObjectCreator.class);
//        DeviceRepository deviceRepository = mock(DeviceRepository.class);
//
//        given(deviceRepository.objectWithSerialNumber(NUID, deviceObjectCreator)).willReturn(expectedDevice);
//
//        DeviceCacheHelper helper = new DeviceCacheHelper(deviceObjectCreator, deviceRepository);
//        Device device = helper.deviceForNuid(NUID);
//        
//        assertSame(device, expectedDevice);
//        verify(deviceRepository).objectWithSerialNumber(NUID, deviceObjectCreator);
//    }
}
