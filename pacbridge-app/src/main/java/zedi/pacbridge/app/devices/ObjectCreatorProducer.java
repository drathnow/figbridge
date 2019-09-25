package zedi.pacbridge.app.devices;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Produces instance of the ObjectCreator<T> class.
 * 
 */
public class ObjectCreatorProducer {

    @Produces
    public DeviceObjectCreator produceCreator(InjectionPoint injectionPoint) {
        return new DeviceObjectCreator(new KeyDecoder());
    }
}
