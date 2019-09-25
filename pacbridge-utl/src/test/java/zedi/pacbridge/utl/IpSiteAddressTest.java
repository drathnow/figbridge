package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IpSiteAddressTest {

    @Test
    public void testToString() throws Exception {
        assertEquals("1.2.3.4/1",
                new IpSiteAddress("1.2.3.4",new Integer(1)).toString());
    }
    
    @Test
    public void shouldClone() throws Exception {
        SiteAddress addressKey = new IpSiteAddress("1.2.3.4",new Integer(1));
        SiteAddress clonedAddressKey = (SiteAddress)addressKey.clone();
        assertNotSame(addressKey, clonedAddressKey);
        assertEquals(addressKey.getAddress(), "1.2.3.4");
        assertEquals(addressKey.getNetworkNumber(), new Integer(1));
    }
    
    
    @Test
    public void testEquals() throws Exception {
        SiteAddress addressKey1 = new IpSiteAddress("1.2.3.4",new Integer(1));
        SiteAddress addressKey2 = new IpSiteAddress("1.2.3.4",new Integer(1));
        SiteAddress addressKey3 = new IpSiteAddress("1.2.3.2",new Integer(1));
        SiteAddress addressKey4 = new IpSiteAddress("1.2.3.4",new Integer(2));
        assertTrue(addressKey1.equals(addressKey2));
        assertFalse(addressKey1.equals(addressKey3));
        assertFalse(addressKey1.equals(addressKey4));
    }
    
    @Test
    public void testCompareTo() throws Exception {
        IpSiteAddress addressKey1 = new IpSiteAddress("1.2.3.1",new Integer(1));
        IpSiteAddress addressKey2 = new IpSiteAddress("1.2.3.2",new Integer(1));
        IpSiteAddress addressKey3 = new IpSiteAddress("1.2.3.3",new Integer(1));
        assertTrue(addressKey1.compareTo(addressKey1) == 0);
        assertTrue(addressKey1.compareTo(addressKey2) < 0);
        assertTrue(addressKey1.compareTo(addressKey3) < 0);
        assertTrue(addressKey2.compareTo(addressKey1) > 0);
        assertTrue(addressKey2.compareTo(addressKey2) == 0);
        assertTrue(addressKey2.compareTo(addressKey3) < 0);
        assertTrue(addressKey3.compareTo(addressKey1) > 0);
        assertTrue(addressKey3.compareTo(addressKey2) > 0);
        assertTrue(addressKey3.compareTo(addressKey3) == 0);
    }
}
