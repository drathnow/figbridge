package zedi.pacbridge.app.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.SystemTime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LostConnectionTracker.class)
public class LostConnectionTrackerTest extends BaseTestCase {

    private static final String THE_MESSAGE = "the message";
    
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private SystemTime systemTime;
    
    @Test
    public void shouldRaiseAlarmAndThenReset() throws Exception {
        
    }
    
    @Test
    public void shouldRecordExceptionsAndRaiseAlarmIfThresholdReached() throws Exception {
        Long now = System.currentTimeMillis();
        Long then = now + TimeUnit.MINUTES.toMillis(2);
        Long thenAgain = now + TimeUnit.MINUTES.toMillis(3);
        Long alarmTime = now + TimeUnit.MINUTES.toMillis(4);
        Map<Long, String> map = new TreeMap<Long, String>();
        Exception exception = mock(Exception.class);
        LostConnectionTracker tracker = new LostConnectionTracker(siteAddress, 3, 5, map);

        given(exception.toString()).willReturn(THE_MESSAGE);

        tracker.recordLostConnection(exception, now);
        assertFalse(tracker.isInAlarm());
        assertFalse(tracker.shouldRaiseAlarm());
        
        tracker.recordLostConnection(exception, then);
        assertFalse(tracker.isInAlarm());
        assertFalse(tracker.shouldRaiseAlarm());

        tracker.recordLostConnection(exception, thenAgain);
        assertFalse(tracker.isInAlarm());
        assertFalse(tracker.shouldRaiseAlarm());
        
        tracker.recordLostConnection(exception, alarmTime);
        assertTrue(tracker.isInAlarm());
        assertTrue(tracker.shouldRaiseAlarm());

        assertTrue(map.containsKey(now));
        assertTrue(map.containsKey(then));
        assertTrue(map.containsKey(thenAgain));
        assertTrue(map.containsKey(alarmTime));
        assertEquals(THE_MESSAGE, map.get(now));
        assertEquals(THE_MESSAGE, map.get(then));
        assertEquals(THE_MESSAGE, map.get(thenAgain));
        assertEquals(THE_MESSAGE, map.get(alarmTime));
    }
    
    @Test
    public void shouldRecordExceptionWhenWithinTimeThresholdAndNotRaiseAlarm() throws Exception {
        Long now = System.currentTimeMillis();
        Long then = now + TimeUnit.MINUTES.toMillis(1);
        Map<Long, String> map = new TreeMap<Long, String>();
        Exception exception = mock(Exception.class);
        LostConnectionTracker tracker = new LostConnectionTracker(siteAddress, 5, 5, map);

        given(exception.toString()).willReturn(THE_MESSAGE);
        given(systemTime.getCurrentTime())
            .willReturn(now)
            .willReturn(then);
        tracker.recordLostConnection(exception, now);
        assertFalse(tracker.isInAlarm());
        assertFalse(tracker.shouldRaiseAlarm());
        tracker.recordLostConnection(exception, then);
        assertFalse(tracker.isInAlarm());
        assertFalse(tracker.shouldRaiseAlarm());
        
        assertTrue(map.containsKey(now));
        assertTrue(map.containsKey(then));
        assertEquals(THE_MESSAGE, map.get(now));
        assertEquals(THE_MESSAGE, map.get(then));
    }

    @Test
    public void shouldRecordFIrstExceptionAndTime() throws Exception {
        Long now = System.currentTimeMillis();
        Map<Long, String> map = new TreeMap<Long, String>();
        Exception exception = mock(Exception.class);
        LostConnectionTracker tracker = new LostConnectionTracker(siteAddress, 5, 5, map);

        given(exception.toString()).willReturn(THE_MESSAGE);
        given(systemTime.getCurrentTime()).willReturn(now);
        tracker.recordLostConnection(exception, now);
        assertFalse(tracker.shouldRaiseAlarm());
        assertFalse(tracker.isInAlarm());
        
        assertTrue(map.containsKey(now));
        assertEquals(THE_MESSAGE, map.get(now));
    }
}
