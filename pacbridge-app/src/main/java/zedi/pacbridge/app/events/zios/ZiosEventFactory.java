package zedi.pacbridge.app.events.zios;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdom2.Element;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventFactory;
import zedi.pacbridge.app.events.EventName;
import zedi.pacbridge.app.events.HandleableEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

/**
 * Factory class that converts JDOM elements into {@link Event} objects.  This class is thread
 * safe.
 *
 */
@ApplicationScoped
public class ZiosEventFactory implements EventFactory {

    private FieldTypeLibrary fieldTypeLibrary;
    private DeviceCache deviceCache;

    public ZiosEventFactory() {
    }
    
    @Inject
    public ZiosEventFactory(FieldTypeLibrary fieldTypeLibrary, DeviceCache deviceCache) {
        this.fieldTypeLibrary = fieldTypeLibrary;
        this.deviceCache = deviceCache;
    }

    @Override
    public String getName() {
        return "ZIOS Event Factory";
    }
    
    /**
     * Returns an {@link Event} for a JDOM Element.
     */
    public HandleableEvent eventForElement(Element element) throws InvalidEventFormatException {
        String eventNameString = element.getAttributeValue("name");
        EventName eventName = ZiosEventName.eventNameForName(eventNameString);
        if (eventName == ZiosEventName.WriteIOPoints)
            return WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache);
        if (eventName == ZiosEventName.DemandPoll)
            return DemandPollEvent.demandPollEventEventForElement(element, deviceCache);
        if (eventName == ZiosEventName.Configure)
            return ConfigureEvent.configureEventForElement(element, fieldTypeLibrary, deviceCache);
        if (eventName == ZiosEventName.Scrub)
            return ScrubEvent.scrubEventForElement(element, deviceCache);
        if (eventName == ZiosEventName.OtadRequest)
            return OtadRequestEvent.otadRequestEventForElement(element, deviceCache);
        return null;
    }
}
