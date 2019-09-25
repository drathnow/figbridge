package zedi.pacbridge.app.events;


public interface Event {
    public static final String ROOT_ELEMENT_NAME = "Event";
    public static final String QUALIFIER_TAG = "qualifier";
    public static final String NAME_TAG = "name";
    public static final String EVENT_ID_TAG = "EventId";
    
    public EventName getEventName();
    public EventQualifier getEventQualifier();
    public Long getEventId();
    public Long getUniqueId();
    public String asXmlString();
}
