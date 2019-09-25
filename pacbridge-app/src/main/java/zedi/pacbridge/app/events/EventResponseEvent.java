package zedi.pacbridge.app.events;

public interface EventResponseEvent extends Event {
    public EventName getEventName();
    public EventQualifier getEventQualifier();
    public Long getEventId();
    public String asXmlString();
}
