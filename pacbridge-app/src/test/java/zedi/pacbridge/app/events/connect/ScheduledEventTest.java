package zedi.pacbridge.app.events.connect;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.jdom2.Element;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.strategies.NextDueTimeStrategy;


public class ScheduledEventTest extends BaseTestCase {

    public static final short EVENT_INDEX1 = 1;
    public static final short EVENT_INDEX2 = 2;
    public static final long START_TIME = System.currentTimeMillis();
    public static final int INTERVAL = 10;
    public static final int DURATION = 20;
    
    @Mock
    private NextDueTimeStrategy dueTimeStrategy;
        
    @Test
    public void shouldReturnNextEventTime() throws Exception {
        SystemTime mockSystemTime = mock(SystemTime.class);
        long currentTime = System.currentTimeMillis();
        long expectedTime = System.currentTimeMillis() + 1000L;
        
        given(mockSystemTime.getCurrentTime()).willReturn(currentTime);
        given(dueTimeStrategy.nextDueTimeFromTime(currentTime)).willReturn(expectedTime);
        
        ScheduledEvent eventDetail = new ScheduledEvent(dueTimeStrategy);
        eventDetail.setSystemTime(mockSystemTime);
        
        long resultTime = eventDetail.nextEventTime();
        assertEquals(expectedTime, resultTime);
        verify(mockSystemTime).getCurrentTime();
        verify(dueTimeStrategy).nextDueTimeFromTime(currentTime);
    }

    @Test
    public void testTakeValuesFromElement() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SystemTime systemTime = mock(SystemTime.class);
        
        Long currentTime = simpleDateFormat.parse("13:56:00 000").getTime();
        Long expectedTime = simpleDateFormat.parse("14:00:00 000").getTime();
        Long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        Long interval = TimeUnit.MINUTES.toMillis(10);

        given(systemTime.getCurrentTime()).willReturn(currentTime);
        
        Element element = new Element(ScheduledEvent.ROOT_ELEMENT_NAME);
        element.addContent(new Element(ScheduledEvent.START_TIME_TAG).setText(""+(startTime/1000L)));
        element.addContent(new Element(ScheduledEvent.INTERVAL_TAG).setText(""+interval/1000L));
        
        ScheduledEvent event = ScheduledEvent.deviceEventForElement(element);
        event.setSystemTime(systemTime);
        
        Long resultTime = event.nextEventTime();
        String msg = "Exp: " 
            + formatter.format(new Date(expectedTime))
            + " Result: " 
            + formatter.format(new Date(resultTime));
        assertEquals(msg, expectedTime, resultTime);
    }
}
