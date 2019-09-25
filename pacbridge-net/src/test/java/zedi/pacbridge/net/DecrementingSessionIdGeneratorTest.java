package zedi.pacbridge.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DecrementingSessionIdGeneratorTest {

    @Test
    public void shouldDecrementToZeroThenReset() {
        DecrementingSessionIdGenerator generator = new DecrementingSessionIdGenerator(10);
        for (int i = 10; i > 5; i--)
            assertEquals(i, generator.nextSessionId().intValue());
        assertEquals(10, generator.nextSessionId().intValue());
    }

}
