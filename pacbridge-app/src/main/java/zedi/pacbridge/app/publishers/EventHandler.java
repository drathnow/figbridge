package zedi.pacbridge.app.publishers;

import zedi.pacbridge.app.events.Event;

public interface EventHandler
{
	void publishEvent(Event event);

}