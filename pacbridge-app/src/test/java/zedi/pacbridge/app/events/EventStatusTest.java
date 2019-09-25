package zedi.pacbridge.app.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;


public class EventStatusTest {

    @Test
    public void testEventStatusForName() throws Exception {
        Field[] fields = EventStatus.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            Method m = c.getMethod("getName", (Class[])null);
            EventStatus controlKey = (EventStatus)f.get(null);
            String keyName = (String)m.invoke(controlKey, (Object[])null);
            assertEquals("Key not found: " 
                        + controlKey.getName(), controlKey, EventStatus.eventStatusForName(keyName));
        }
        
        assertNull(EventStatus.eventStatusForName("spooge"));
    }
}
