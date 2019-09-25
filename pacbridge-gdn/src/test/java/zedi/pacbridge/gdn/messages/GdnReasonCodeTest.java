package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;


public class GdnReasonCodeTest {

    @Test
    public void testReasonCodeForName() throws Exception {
        Field[] fields = GdnReasonCode.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            if (c.equals(GdnReasonCode.class)) {
                Method m = c.getMethod("getName", (Class[])null);
                GdnReasonCode reasonCode = (GdnReasonCode)f.get(null);
                String keyName = (String)m.invoke(reasonCode, (Object[])null);
                assertEquals("Key not found: " + reasonCode.getName(), reasonCode, GdnReasonCode.reasonCodeForName(keyName));
            }
        }
    }

    @Test
    public void testReasonCodeForTypeNumber() throws Exception {
        Field[] fields = GdnReasonCode.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            if (c == GdnReasonCode.class) {
                Method m = c.getMethod("getNumber", (Class[])null);
                GdnReasonCode reasonCode = (GdnReasonCode)f.get(null);
                Integer keyName = (Integer)m.invoke(reasonCode, (Object[])null);
                assertEquals("Key not found: " + reasonCode.getName(), reasonCode, GdnReasonCode.reascodeForReasonNumber(keyName));
            }
        }
    }

}
