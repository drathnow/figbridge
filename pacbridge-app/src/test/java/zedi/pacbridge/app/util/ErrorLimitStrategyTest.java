package zedi.pacbridge.app.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SystemTime;

public class ErrorLimitStrategyTest extends BaseTestCase {
    private static final Integer ERROR_TIME_LIMIT = 2; // Seconds
    private static final Integer ERROR_COUNT_LIMIT = 3;
    private static final Integer ERROR_SANITY_TIME = 5; // Seconds;

    @Test
    public void shouldBeAbleToLogAnotherError() throws Exception {
        long now = System.currentTimeMillis();
        SystemTime systemTime = mock(SystemTime.class);
        
        given(systemTime.getCurrentTime())
            .willReturn(now)
            .willReturn(now+=1000)
            .willReturn(now+=11000)
            .willReturn(now+=11100);
        
        ErrorLimitStrategy strategy = new ErrorLimitStrategy(ERROR_TIME_LIMIT, ERROR_COUNT_LIMIT, ERROR_SANITY_TIME, systemTime);
        
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
    }
    
    @Test
    public void shouldNoBeAbleToLogAnotherError() throws Exception {
        ErrorLimitStrategy strategy = new ErrorLimitStrategy(ERROR_TIME_LIMIT, ERROR_COUNT_LIMIT, ERROR_SANITY_TIME);
        
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
        
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());

        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
        
        strategy.incrementErrorCount();
        assertFalse(strategy.canLogAnotherError());

        strategy.incrementErrorCount();
        assertFalse(strategy.canLogAnotherError());
        Thread.sleep(ERROR_SANITY_TIME*1000 + 100);
        
        assertTrue(strategy.canLogAnotherError());
        strategy.incrementErrorCount();
        assertTrue(strategy.canLogAnotherError());
    }
}
