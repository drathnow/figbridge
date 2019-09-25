package zedi.pacbridge.utl.stats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class MovingAverageTest extends BaseTestCase {

    private final static int SIZE = 5;
    private static final double DELTA = 0.0;

    @Test
    public void testInitial() {
        MovingAverage movingAverage = new MovingAverage(SIZE);
        assertEquals(0d, movingAverage.getAverage(), DELTA);
    }

    @Test
    public void testOneValue() {
        MovingAverage movingAverage = new MovingAverage(SIZE);
        movingAverage.addSample(3.5d);
        assertEquals(3.5d, movingAverage.getAverage(), DELTA);
    }

    @Test
    public void testTwoValues() {
        MovingAverage movingAverage = new MovingAverage(SIZE);
        movingAverage.addSample(3.5d);
        movingAverage.addSample(1.8d);
        assertEquals(2.65d, movingAverage.getAverage(), DELTA);
    }

    @Test
    public void testForceOverWrite() {
        MovingAverage movingAverage = new MovingAverage(2);
        movingAverage.addSample(3.5d);
        movingAverage.addSample(1.8d);
        assertEquals(2.65d, movingAverage.getAverage(), 0.001);
        
        movingAverage.addSample(5.6d);
        assertEquals(3.7d, movingAverage.getAverage(), 0.001);
        
        movingAverage.addSample(1.3d);
        assertEquals(3.45d, movingAverage.getAverage(), 0.001);
    }

}
