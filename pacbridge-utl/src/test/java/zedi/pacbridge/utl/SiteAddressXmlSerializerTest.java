package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class SiteAddressXmlSerializerTest extends BaseTestCase {
    private static final String IP_ADDRESS_XML =
                                  "<SiteAddress>"
                                + "    <IpAddress>1.2.3.4</IpAddress>"
                                + "    <NetworkNumber>21</NetworkNumber>"
                                + "</SiteAddress>";
    
    private static final String NUID_ADDRESS_XML =
                                  "<SiteAddress>"
                                + "    <Nuid>1234</Nuid>"
                                + "    <NetworkNumber>21</NetworkNumber>"
                                + "</SiteAddress>";
    
    @Test
    public void shouldParseIpAddressXml() throws Exception {
        SiteAddressXmlSerializer serializer = new SiteAddressXmlSerializer();
        Element element = JDomUtilities.elementForXmlString(IP_ADDRESS_XML);
        IpSiteAddress address = (IpSiteAddress)serializer.siteAddressFor(element);
        assertEquals("1.2.3.4", address.getAddress());
        assertEquals(21, address.getNetworkNumber().intValue());
    }

    @Test
    public void shouldParseNuidAddressXml() throws Exception {
        SiteAddressXmlSerializer serializer = new SiteAddressXmlSerializer();
        Element element = JDomUtilities.elementForXmlString(NUID_ADDRESS_XML);
        NuidSiteAddress address = (NuidSiteAddress)serializer.siteAddressFor(element);
        assertEquals("1234", address.getAddress());
        assertEquals("1234", address.getNetworkUnitId());
        assertEquals(21, address.getNetworkNumber().intValue());
    }
}
