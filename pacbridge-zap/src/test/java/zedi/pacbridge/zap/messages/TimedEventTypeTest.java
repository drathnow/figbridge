package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class TimedEventTypeTest extends BaseTestCase {

    @Test
    public void shouldReturnPollEventForPollEventNumber() throws Exception {
        assertEquals(TimedEventType.Poll, TimedEventType.timedEventTypeForNumber(TimedEventType.POLL_EVENT_NUMBER));
    }

    @Test
    public void shouldReturnReservedEventForReservedEventNumber() throws Exception {
        assertEquals(TimedEventType.Reserved, TimedEventType.timedEventTypeForNumber(TimedEventType.RESERVED_EVENT_NUMBER));
    }

    @Test
    public void shouldReturnReportEventForReportEventNumber() throws Exception {
        assertEquals(TimedEventType.Report, TimedEventType.timedEventTypeForNumber(TimedEventType.REPORT_EVENT_NUMBER));
    }

    @Test
    public void shouldReturnRebootEventForRebootEventNumber() throws Exception {
        assertEquals(TimedEventType.Reboot, TimedEventType.timedEventTypeForNumber(TimedEventType.REBOOT_EVENT_NUMBER));
    }

    @Test
    public void shouldReturnTimeSycnEventForTimeSyncEventNumber() throws Exception {
        assertEquals(TimedEventType.TimeSync, TimedEventType.timedEventTypeForNumber(TimedEventType.TIMESYNCH_EVEN_NUMBER));
    }
    
    @Test
    public void shouldNetworkStatusEventForTimeSyncEventNumber() throws Exception {
        assertEquals(TimedEventType.NetworkStatus, TimedEventType.timedEventTypeForNumber(TimedEventType.NETSTATUS_EVENT_NUMBER));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfUnknownEvenTypeNumberPassed() throws Exception {
        TimedEventType.timedEventTypeForNumber(99);
    }
}

