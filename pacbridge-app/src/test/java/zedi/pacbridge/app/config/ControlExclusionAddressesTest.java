package zedi.pacbridge.app.config;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public class ControlExclusionAddressesTest extends BaseTestCase {

    private static final String XML_ELEMENT = 
              "<ControlExclusionAddresses>"
            + "     <Address ip=\"1.2.3.4\" number=\"17\" />"
            + "     <Address nuid=\"1234\" number=\"18\" />"
            + "</ControlExclusionAddresses>";
    
    @Test
    public void shouldParseElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_ELEMENT);
        Set<SiteAddress> list = ControlExclusionAddresses.controlExclusionAddressesForJDomElement(element);

        IpSiteAddress address1 = new IpSiteAddress("1.2.3.4", 17);
        NuidSiteAddress address2 = new NuidSiteAddress("1234", 18);
        assertTrue(list.contains(address1));
        assertTrue(list.contains(address2));
    }
}
