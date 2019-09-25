package zedi.pacbridge.app.events.connect;

import java.io.Serializable;

import org.jdom2.Element;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventName;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.gdn.GdnDataTypeFactory;

public abstract class ConnectEvent implements Event, Serializable {
    private static final long serialVersionUID = 10001L;    
    
    private EventQualifier eventQualifier;
    private ConnectEventName eventName;
    private Long eventId;
    protected GdnDataTypeFactory dataTypeFactory;
    
    protected ConnectEvent(ConnectEventName eventName, Long eventId) {
        this.eventName = eventName;
        this.eventQualifier = EventQualifier.Connect;
        this.eventId = eventId;
        this.dataTypeFactory = new GdnDataTypeFactory();
    }
    
    protected ConnectEvent(ConnectEventName eventName, Element element) {
        this(eventName, Long.valueOf(element.getChildText(EVENT_ID_TAG)));
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
        return new Element(ROOT_ELEMENT_NAME)
                    .setAttribute(NAME_TAG, eventName.getName())
                    .setAttribute(QUALIFIER_TAG, eventQualifier.getName())
                    .addContent(new Element(EVENT_ID_TAG).setText(eventId == null ? null : eventId.toString()));
    }
}
