package zedi.fg.tester.configs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BaseTestConfiguration
{
	protected static AtomicLong eventId = new AtomicLong(1);
	protected static AtomicLong correlationId = new AtomicLong(1);
	protected static AtomicInteger id = new AtomicInteger(100);
}
