package zedi.pacbridge.app.events.zios;

import java.io.Serializable;
import java.util.UUID;

import org.jdom2.Element;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventName;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.zap.values.ZapDataTypeFactory;

public abstract class ZiosEvent implements Event, Serializable {
    private static final long serialVersionUID = 10001L;    
    protected static ZapDataTypeFactory dataTypeFactory = new ZapDataTypeFactory();
    
    public static final String ROOT_ELEMENT_NAME = "Event";
    public static final String NUID_TAG = "Nuid";
    public static final String NETWORK_NUMBER_TAG = "NetworkNumber";
    
    private EventQualifier eventQualifier;
    private ZiosEventName eventName;
    private Long eventId;
    private Long uniqueId;

    protected ZiosEvent(ZiosEventName eventName) {
        this.eventName = eventName;
        this.eventQualifier = EventQualifier.ZIOS;
        this.uniqueId = UUID.randomUUID().getMostSignificantBits();
    }

    protected ZiosEvent(ZiosEventName eventName, Long eventId) {
        this(eventName);
        this.eventId = eventId;
    }
    
    protected ZiosEvent(ZiosEventName eventName, Element element) {
        this(eventName, Long.valueOf(element.getChildText(EVENT_ID_TAG)));
    }
    
    @Override
    public Long getUniqueId() {
        return uniqueId;
    }
   
    @Override
    public EventQualifier getEventQualifier() {
        return eventQualifier;
    }
    
    @Override
    public EventName getEventName() {
        return eventName;
    }
    
    @Override
    public Long getEventId() {
        return eventId;
    }
    
    protected Element rootElement() {
        Element rootElement = new Element(ROOT_ELEMENT_NAME)
                    .setAttribute(NAME_TAG, eventName.getName())
                    .setAttribute(QUALIFIER_TAG, eventQualifier.getName());
        Element eventIdElement = new Element(EVENT_ID_TAG);
        if (eventId != null)
            eventIdElement.setText(eventId.toString());
        rootElement.addContent(eventIdElement);
        return rootElement;
    }
    
    protected static Element eventElementFromRoot(ZiosEventName eventName, Element rootElement) throws InvalidEventFormatException {
        return requireElementFromRoot(eventName.getName(), rootElement);
    }
    
    protected static Element requireElementFromRoot(String elementName, Element rootElement) throws InvalidEventFormatException {
        Element element = rootElement.getChild(elementName);
        if (element == null)
            throw new InvalidEventFormatException("Event does not contain required element: " + elementName);
        return element;
    }
}
