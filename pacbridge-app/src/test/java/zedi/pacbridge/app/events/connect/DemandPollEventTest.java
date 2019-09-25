package zedi.pacbridge.app.events.connect;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.IpSiteAddress;

public class DemandPollEventTest extends BaseTestCase {
    private static final Integer INDEX = 1;
    private static final Integer POLLSET = 2;
    private static final Long EVENT_ID = 123L;
    private static final String ADDRESS = "1.2.3.4";
    private static final Integer NETWORK_NUMBER = 17;
    private static final Integer FIRMWARE_VERSION = 123;
    
    @Test
    public void testAsXmlString() throws Exception {
        DemandPollEvent event = new DemandPollEvent(INDEX, POLLSET, EVENT_ID, new IpSiteAddress(ADDRESS, NETWORK_NUMBER), FIRMWARE_VERSION, ADDRESS);
        System.out.println("XML:" + event.asXmlString());
    }

}