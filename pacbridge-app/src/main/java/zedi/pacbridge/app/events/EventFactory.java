package zedi.pacbridge.app.events;

import org.jdom2.Element;

public interface EventFactory {
    public String getName();
    public HandleableEvent eventForElement(Element element) throws InvalidEventFormatException;
}
