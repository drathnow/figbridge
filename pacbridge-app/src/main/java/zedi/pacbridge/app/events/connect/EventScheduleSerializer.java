package zedi.pacbridge.app.events.connect;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.gdn.pac.EventSchedule;

public class EventScheduleSerializer {
    public static final String ROOT_ELEMENT_NAME = "EventSchedule";
    public static final String DEVIC_EVENT_TAG = "DeviceEventBasic";
    
    public static EventSchedule<ScheduledEvent> eventScheduleForElement(Element element) {
        List<Element> eventElements = element.getChildren(DEVIC_EVENT_TAG);
        List<ScheduledEvent> eventList = new ArrayList<ScheduledEvent>();
        for (Element eventElement : eventElements) {
            ScheduledEvent event = ScheduledEvent.deviceEventForElement(eventElement);
            eventList.add(event);
        }
        return new EventSchedule<ScheduledEvent>(eventList);
    }

    public static Element elementForEventSchedule(EventSchedule<ScheduledEvent> eventSchedule) {
        Element element = new Element(ROOT_ELEMENT_NAME);
        for (ScheduledEvent event : eventSchedule.events())
            element.addContent(new Element(DEVIC_EVENT_TAG).addContent(event.asElement()));
        return element;
    }
}
