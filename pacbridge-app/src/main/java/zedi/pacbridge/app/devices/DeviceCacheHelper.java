package zedi.pacbridge.app.devices;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.domain.repositories.DeviceRepository;

public class DeviceCacheHelper {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCacheHelper.class.getName());
    
    private DeviceObjectCreator deviceObjectCreator;
    private DeviceRepository deviceRepository;

    public DeviceCacheHelper() {
    }
    
    @Inject
    public DeviceCacheHelper(DeviceObjectCreator deviceObjectCreator, DeviceRepository deviceRepository) {
        this.deviceObjectCreator = deviceObjectCreator;
        this.deviceRepository = deviceRepository;
    }

    public Device deviceForNuid(String nuid) {
        try {
            return deviceRepository.objectWithSerialNumber(nuid, deviceObjectCreator);
        } catch (Exception e) {
            logger.error("Unable to lookup network unit id '" + nuid + "': ", e);
        }
        return null;
    }
}
