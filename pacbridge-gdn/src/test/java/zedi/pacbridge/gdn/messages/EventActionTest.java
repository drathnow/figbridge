package zedi.pacbridge.gdn.messages;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

public class EventActionTest {

    @Test
    public void testEventActionForEventNumber() throws Exception {
        Field[] fields = EventAction.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            try {
                Method m = c.getMethod("getActionNumber", (Class[])null);
                EventAction eventAction = (EventAction)f.get(null);
                Integer keyName = (Integer)m.invoke(eventAction, (Object[])null);
                
                assertEquals("Key not found: " + eventAction.getName(), eventAction, EventAction.eventActionForActionNumber(keyName.intValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testEventActionForEventName() throws Exception {
        Field[] fields = EventAction.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            try {
                Method m = c.getMethod("getName", (Class[])null);
                EventAction eventAction = (EventAction)f.get(null);
                String keyName = (String)m.invoke(eventAction, (Object[])null);
                
                assertEquals("Key not found: " + eventAction.getName(), eventAction, EventAction.eventActionForActionName(keyName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
