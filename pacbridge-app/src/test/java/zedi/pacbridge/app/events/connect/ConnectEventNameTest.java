package zedi.pacbridge.app.events.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import zedi.pacbridge.app.events.EventStatus;


public class ConnectEventNameTest {

    @Test
    public void testEventNameForName() throws Exception {
        Field[] fields = ConnectEventName.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            Method method = c.getMethod("getName", (Class[])null);
            ConnectEventName eventName = (ConnectEventName)f.get(null);
            String keyName = (String)method.invoke(eventName, (Object[])null);
            assertEquals("Key not found: " 
                        + eventName.getName(), eventName, ConnectEventName.eventNameForName(keyName));
        }
        
        assertNull(EventStatus.eventStatusForName("spooge"));
    }    
}
