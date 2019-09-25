package zedi.pacbridge.net.controls;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;


public class ControlStatusTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldHurlWithUnknownStatusName() throws Exception {
        ControlStatus.eventStatusForName("spooge");
    }
    
    @Test
    public void testEventStatusForName() throws Exception {
        Field[] fields = ControlStatus.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            Method m = c.getMethod("getName", (Class[])null);
            ControlStatus controlKey = (ControlStatus)f.get(null);
            String keyName = (String)m.invoke(controlKey, (Object[])null);
            assertEquals("Key not found: " 
                        + controlKey.getName(), controlKey, ControlStatus.eventStatusForName(keyName));
        }
    }
}
