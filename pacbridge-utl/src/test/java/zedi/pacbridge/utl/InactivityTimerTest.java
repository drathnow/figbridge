package zedi.pacbridge.utl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class InactivityTimerTest {

    @Test
    public void shouldWorkProperly() throws InterruptedException {
        Long now = 100L;
        Long then = now + 50;
        Long thenAgain = now + 110;
        SystemTime systemTime = mock(SystemTime.class);
        
        given(systemTime.getCurrentTime())
            .willReturn(now)
            .willReturn(then)
            .willReturn(thenAgain);
        
        InactivityTimer inactivityTimer = new InactivityTimer(100, systemTime);
        inactivityTimer.start();
        assertFalse(inactivityTimer.isExpired());
        assertTrue(inactivityTimer.isExpired());
    }
}