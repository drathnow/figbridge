package zedi.pacbridge.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IncrementingSessionIdGeneratorTest {

    @Test
    public void shouldReturnUpToMaxAndThenZero() {
        IncrementingSessionIdGenerator generator = new IncrementingSessionIdGenerator(10);
        for (int i = 1; i < 5; i++)
            assertEquals(i, generator.nextSessionId().intValue());
        assertEquals(1, generator.nextSessionId().intValue());
    }
}
