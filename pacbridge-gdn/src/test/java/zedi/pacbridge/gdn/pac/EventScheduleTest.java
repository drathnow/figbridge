package zedi.pacbridge.gdn.pac;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.gdn.messages.NextEventTime;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;


@SuppressWarnings({"rawtypes", "unchecked"})
public class EventScheduleTest extends BaseTestCase {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");

    @Test
    public void shouldCalculateNextEventTime() throws Exception {
        Long expectedTime = simpleDateFormat.parse("13:45:00 000").getTime();
        Long olderTime =  simpleDateFormat.parse("13:50:00 000").getTime();

        NextEventTime deviceEvent1 = mock(NextEventTime.class);
        NextEventTime deviceEvent2 = mock(NextEventTime.class);

        given(deviceEvent1.nextEventTime()).willReturn(expectedTime);
        given(deviceEvent2.nextEventTime()).willReturn(olderTime);

        EventSchedule eventSchedule = new EventSchedule(Arrays.asList(deviceEvent1, deviceEvent2));
        assertEquals(expectedTime, eventSchedule.nextEventTime());
        verify(deviceEvent1).nextEventTime();
        verify(deviceEvent2).nextEventTime();
    }

    @Test
    public void shouldReturnNoFutureEventIfNoFutureEventFound() throws Exception {
        NextEventTime deviceEvent1 = mock(NextEventTime.class);
        NextEventTime deviceEvent2 = mock(NextEventTime.class);

        given(deviceEvent1.nextEventTime()).willReturn(Utilities.DISTANT_FUTURE_INMILLIS);
        given(deviceEvent2.nextEventTime()).willReturn(Utilities.DISTANT_FUTURE_INMILLIS);

        EventSchedule eventSchedule = new EventSchedule(Arrays.asList(deviceEvent1, deviceEvent2));
        assertEquals(EventSchedule.NO_FUTURE_EVENT, eventSchedule.nextEventTime().longValue());
        verify(deviceEvent1).nextEventTime();
        verify(deviceEvent2).nextEventTime();
    }
}