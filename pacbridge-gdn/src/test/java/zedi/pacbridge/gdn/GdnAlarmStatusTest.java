package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class GdnAlarmStatusTest extends BaseTestCase {
    
    @Test
    public void testIsDataUnavailable() throws Exception {
        assertTrue(GdnAlarmStatus.RTUBackoff.isDataUnavailable());
        assertTrue(GdnAlarmStatus.RTUError.isDataUnavailable());
        assertTrue(GdnAlarmStatus.RTUOverflow.isDataUnavailable());
        assertTrue(GdnAlarmStatus.RTUTimeout.isDataUnavailable());
        assertFalse(GdnAlarmStatus.OK.isDataUnavailable());
        assertFalse(GdnAlarmStatus.Low.isDataUnavailable());
        assertFalse(GdnAlarmStatus.LowLow.isDataUnavailable());
        assertFalse(GdnAlarmStatus.High.isDataUnavailable());
        assertFalse(GdnAlarmStatus.HighHigh.isDataUnavailable());
        assertFalse(GdnAlarmStatus.NaN.isDataUnavailable());
    }
    
    @Test
    public void testAlarmStatusForTypeNumber() throws Exception {
        assertEquals(GdnAlarmStatus.OK, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.OK.getTypeNumber()));
        assertEquals(GdnAlarmStatus.Low, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.Low.getTypeNumber()));
        assertEquals(GdnAlarmStatus.LowLow, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.LowLow.getTypeNumber()));
        assertEquals(GdnAlarmStatus.High, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.High.getTypeNumber()));
        assertEquals(GdnAlarmStatus.HighHigh, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.HighHigh.getTypeNumber()));
        assertEquals(GdnAlarmStatus.RTUTimeout, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.RTUTimeout.getTypeNumber()));
        assertEquals(GdnAlarmStatus.RTUBackoff, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.RTUBackoff.getTypeNumber()));
        assertEquals(GdnAlarmStatus.RTUError, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.RTUError.getTypeNumber()));
        assertEquals(GdnAlarmStatus.RTUOverflow, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.RTUOverflow.getTypeNumber()));
        assertEquals(GdnAlarmStatus.NaN, GdnAlarmStatus.alarmStatusForAlarmStatusNumber(GdnAlarmStatus.NaN.getTypeNumber()));
        assertNull(GdnAlarmStatus.alarmStatusForAlarmStatusNumber(100));
    }

    @Test
    public void testAlarmTypeForName() throws Exception {
        Field[] fields = GdnAlarmStatus.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            if (c == GdnAlarmStatus.class) {
                Method m = c.getMethod("getName", (Class[])null);
                GdnAlarmStatus alarmStatus = (GdnAlarmStatus)f.get(null);
                String keyName = (String)m.invoke(alarmStatus, (Object[])null);
                assertEquals("Key not found: " 
                        + alarmStatus.getName(), alarmStatus, GdnAlarmStatus.alarmStatusForName(keyName));
            }
        }
    }
}
