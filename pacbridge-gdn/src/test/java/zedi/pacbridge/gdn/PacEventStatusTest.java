package zedi.pacbridge.gdn;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;


public class PacEventStatusTest {

    @Test
    public void testEventStatusForStatusNumber() throws Exception {
        Field[] fields = PacEventStatus.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            try {
                Method m = c.getMethod("getStatusNumber", (Class[])null);
                PacEventStatus eventStatus = (PacEventStatus)f.get(null);
                Integer keyName = (Integer)m.invoke(eventStatus, (Object[])null);
                assertEquals("Key not found: " + eventStatus.getStatusNumber(), eventStatus, PacEventStatus.eventStatusForStatusNumber(keyName.intValue()));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
        
    }
}
