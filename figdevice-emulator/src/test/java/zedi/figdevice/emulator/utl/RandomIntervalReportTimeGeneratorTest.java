package zedi.figdevice.emulator.utl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class RandomIntervalReportTimeGeneratorTest extends BaseTestCase {

    @Test
    public void shouldPrintRandTime() throws Exception {
        RandomIntervalReportTimeGenerator timeGenerator = new RandomIntervalReportTimeGenerator(60);
        for (int i = 1000; i > 0; i--) {
            Integer seconds = timeGenerator.secondsUntilNextReport(); 
            assertTrue(seconds >= 0);
            assertTrue(seconds < 60);
        }
    }
}
