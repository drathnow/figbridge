package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class AlarmTypeTest extends BaseTestCase {

    @Test
    public void testAlarmTypeForName() throws Exception {
        Field[] fields = AlarmType.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            Method m = c.getMethod("getName", (Class[])null);
            AlarmType alarmType = (AlarmType)f.get(null);
            String keyName = (String)m.invoke(alarmType, (Object[])null);
            assertEquals("Key not found: " + alarmType.getName(), alarmType, AlarmType.alarmTypeForName(keyName));
        }
    }

    @Test
    public void testAlarmTypeForAlarmTypeNumber() throws Exception {
        Field[] fields = AlarmType.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            Method m = c.getMethod("getTypeNumber", (Class[])null);
            AlarmType alarmType = (AlarmType)f.get(null);
            Integer keyName = (Integer)m.invoke(alarmType, (Object[])null);
            assertEquals("Key not found: " + alarmType.getName(), alarmType, AlarmType.alarmTypeForTypeNumber(keyName));
        }
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("low",AlarmType.Low.getName());
        assertEquals("lowLow", AlarmType.LowLow.getName());
        assertEquals("high", AlarmType.High.getName());
        assertEquals("highHigh", AlarmType.HighHigh.getName());
        assertEquals("dataUnavailable", AlarmType.DataUnavailable.getName());
    }

}
