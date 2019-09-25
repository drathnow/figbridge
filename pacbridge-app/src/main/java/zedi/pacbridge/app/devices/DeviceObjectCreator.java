package zedi.pacbridge.app.devices;

import java.io.Serializable;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.domain.repositories.ObjectCreator;
import zedi.pacbridge.utl.IntegerSystemProperty;

public class DeviceObjectCreator implements ObjectCreator<Device>, Serializable {
    private static final long serialVersionUID = 1001L;
    private static final Logger logger = LoggerFactory.getLogger(DeviceObjectCreator.class.getName());

    public static final String DEFAULT_NETWORK_NUMBER_PROPERTY_NAME = "pacbridge.defaultNetworkNumber";
    public static final Integer DEFAULT_NETWORK_NUMBER = 17;
    public static IntegerSystemProperty DEFAULT_NETWORK_NUMBER_PROPERTY = new IntegerSystemProperty(DEFAULT_NETWORK_NUMBER_PROPERTY_NAME, DEFAULT_NETWORK_NUMBER);
    
    private KeyDecoder keyDecoder;
    
    public DeviceObjectCreator(KeyDecoder keyDecoder) {
        this.keyDecoder = keyDecoder;
    }

    @Override
    public Device objectForStuff(String serialNumber, byte[] hashBytes, Integer networkNumber, Timestamp lastUpdateTime) {
        try {
            Integer netNumber = networkNumber == 0 ? DEFAULT_NETWORK_NUMBER_PROPERTY.currentValue() : networkNumber;
            byte[] secretKey = keyDecoder.decodedBytesForBase64EncodedBytes(hashBytes);
            return new Device(serialNumber, secretKey, netNumber, lastUpdateTime);
        } catch (SecretKeyDecoderException e) {
            logger.warn("Unable to decode secret key for device '" + serialNumber + "': " + e.toString());
            return null;
        }
    }
}
