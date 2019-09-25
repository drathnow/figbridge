package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DeltaTimeTest extends BaseTestCase {

    @Test
    public void shouldConvertDeltaTimeStringToSeconds() throws Exception {
        assertEquals(10, DeltaTime.deltaTimeStringToSeconds("00:00:10").intValue());
        assertEquals(65, DeltaTime.deltaTimeStringToSeconds("00:01:05").intValue());
        assertEquals(60, DeltaTime.deltaTimeStringToSeconds("00:01:00").intValue());
        assertEquals(91998, DeltaTime.deltaTimeStringToSeconds("25:33:18").intValue());
        assertEquals(523998, DeltaTime.deltaTimeStringToSeconds("5 25:33:18").intValue());
    }
    
    @Test
    public void shouldFormatSecondsAsDeltaTimeString() throws Exception {
        assertEquals("00:00:10", DeltaTime.deltaTimeStringForSeconds(10));
        assertEquals("00:01:05", DeltaTime.deltaTimeStringForSeconds(65));
        assertEquals("1 01:33:18", DeltaTime.deltaTimeStringForSeconds(91998));
        assertEquals("6 01:33:18", DeltaTime.deltaTimeStringForSeconds(523998));
    }
}
