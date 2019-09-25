package zedi.pacbridge.utl.strategies;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class NextDueTimeStrategyTest extends BaseTestCase {
    private static final Long dueWindow = 30000L;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-DDD HH:mm:ss");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
    
    @Test
    public void testNextEventTime() throws Exception {
        Long currentTime = simpleDateFormat.parse("13:56:00 000").getTime();
        Long expectedTime = simpleDateFormat.parse("14:00:00 000").getTime();
        Long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        Long interval = TimeUnit.MINUTES.toMillis(10);
        
        NextDueTimeStrategy eventDetail = new NextDueTimeStrategy(startTime, new Integer(interval.intValue()/1000), dueWindow);
        
        Long resultTime = eventDetail.nextDueTimeFromTime(currentTime);
        String msg = "Exp: " 
            + formatter.format(new Date(expectedTime))
            + " Result: " 
            + formatter.format(resultTime);
        assertEquals(msg, expectedTime, resultTime);
    }
    
    @Test
    public void nextEventTimeFromTime3() throws Exception {
        Long currentTime = simpleDateFormat.parse("13:49:00 000").getTime();
        Long expectedTime = simpleDateFormat.parse("14:0:00 000").getTime();
        Long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        Long interval = TimeUnit.MINUTES.toMillis(120);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }
    
    @Test
    public void nextEventTimeFromTime2() throws Exception {
        Long currentTime = simpleDateFormat.parse("13:49:00 000").getTime();
        Long expectedTime = simpleDateFormat.parse("13:50:00 000").getTime();
        Long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        Long interval = TimeUnit.MINUTES.toMillis(10);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }

    @Test
    public void nextEventTimeFromTime1() throws Exception {
        Long currentTime = simpleDateFormat.parse("13:56:00 000").getTime();
        Long expectedTime = simpleDateFormat.parse("14:00:00 000").getTime();
        Long startTime = simpleDateFormat.parse("12:00:00 000").getTime();
        Long interval = TimeUnit.MINUTES.toMillis(10);
        
        assertCorrectTimes(expectedTime, currentTime, startTime, interval);
    }
        
    private void assertCorrectTimes(Long expectedTime, Long currentTime, Long startTime, Long interval) {
        NextDueTimeStrategy eventDetail = new NextDueTimeStrategy(startTime, (int)(interval/1000L), dueWindow);
        long resultTime = eventDetail.nextDueTimeFromTime(currentTime);
        String msg = "Exp: " 
            + formatter.format(new Date(expectedTime))
            + " Result: " 
            + formatter.format(resultTime);
        assertEquals(msg, expectedTime.longValue(), resultTime);
    }

    
}
