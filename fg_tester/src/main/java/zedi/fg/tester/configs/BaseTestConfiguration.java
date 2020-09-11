package zedi.fg.tester.configs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

public abstract class BaseTestConfiguration
{
	public static final Integer EVENT_ID = 40;
	
	protected static AtomicLong eventId = new AtomicLong(1);
	protected static AtomicLong correlationId = new AtomicLong(1);
	protected static AtomicInteger id = new AtomicInteger(100);
	
	protected FieldTypeLibrary fieldTypeLibrary;

	protected BaseTestConfiguration(FieldTypeLibrary fieldTypeLibrary)
	{
		this.fieldTypeLibrary = fieldTypeLibrary;
	}

	protected Field<?> fieldForFieldNameAndValue(String fieldName, Object value)
	{
		FieldType fieldType = fieldTypeLibrary.fieldTypeForName(fieldName);
		assert(fieldType != null);
		return Field.fieldForFieldTypeAndValue(fieldType, value);
	}
}
