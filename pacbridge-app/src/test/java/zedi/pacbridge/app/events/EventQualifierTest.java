package zedi.pacbridge.app.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

public class EventQualifierTest {

    @Test
    public void testControlServiceKeyForKeyName() throws Exception {
        Field[] fields = EventQualifier.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            try {
                Method m = c.getMethod("getName", (Class[])null);
                EventQualifier controlKey = (EventQualifier)f.get(null);
                String keyName = (String)m.invoke(controlKey, (Object[])null);
                
                assertEquals("Key not found: " + controlKey.getName(), controlKey, EventQualifier.eventQualifierForName(keyName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assertNull(EventQualifier.eventQualifierForName("spooge"));
        
    }
    
}