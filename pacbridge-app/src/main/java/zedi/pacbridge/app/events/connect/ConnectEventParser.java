package zedi.pacbridge.app.events.connect;

import org.jdom2.Element;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventFactory;
import zedi.pacbridge.app.events.HandleableEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;

/**
 * Factory class that converts JDOM elements into {@link Event} objects.  This class is thread
 * safe.
 *
 */
public class ConnectEventParser implements EventFactory {

    @Override
    public String getName() {
        return "Connect Event Parser";
    }
    
    /**
     * Returns an {@link Event} for a JDOM Element.
     */
    public HandleableEvent eventForElement(Element element) throws InvalidEventFormatException {
//        String eventNameString = element.getAttributeValue("name");
//        EventName eventName = ConnectEventName.eventNameForName(eventNameString);
        return null;
    }
}
