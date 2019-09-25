package zedi.figdevice.emulator.utl;

import static org.junit.Assert.assertSame;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class TrackingContainerTest extends BaseTestCase {

    @Test
    public void shouldOrderThemSelves() throws Exception {
        TrackingContainer container1 = new TrackingContainer(1, 10, null);
        TrackingContainer container2 = new TrackingContainer(2, 20, null);
        TrackingContainer container3 = new TrackingContainer(3, 30, null);
        TrackingContainer container4 = new TrackingContainer(4, 40, null);
        
        SortedSet<TrackingContainer> set = new TreeSet<>();
        
        set.add(container4);
        set.add(container2);
        set.add(container1);
        set.add(container3);
        
        Iterator<TrackingContainer> iter = set.iterator();
        assertSame(container1, iter.next());
        assertSame(container2, iter.next());
        assertSame(container3, iter.next());
        assertSame(container4, iter.next());
    }
    
}
