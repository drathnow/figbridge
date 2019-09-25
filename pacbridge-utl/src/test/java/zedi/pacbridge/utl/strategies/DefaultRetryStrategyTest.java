package zedi.pacbridge.utl.strategies;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DefaultRetryStrategyTest extends BaseTestCase {

    @Test
    public void shouldUseValuePassedFromConstructor() throws Exception {
        DefaultRetryStrategy controlRetryStrategy = new DefaultRetryStrategy(10);
        assertTrue(controlRetryStrategy.canRetryOperationAfterNumberOfAttempts(7));
        assertFalse(controlRetryStrategy.canRetryOperationAfterNumberOfAttempts(10));
    }
}
