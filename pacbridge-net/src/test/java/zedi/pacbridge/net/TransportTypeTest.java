package zedi.pacbridge.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransportTypeTest {

    @Test
    public void shouldNowWhoIAm() {
        assertTrue(TransportType.UDP.isUdp());
        assertFalse(TransportType.UDP.isTcp());
        assertTrue(TransportType.TCP.isTcp());
        assertFalse(TransportType.TCP.isUdp());
    }

}
