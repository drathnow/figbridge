package zedi.pacbridge.app.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.net.IdentityType;
import zedi.pacbridge.app.zap.ZapProtocolConfig;
import zedi.pacbridge.net.TransportType;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class NetworkConfigTest extends BaseTestCase {

    private static final String XML_ELEMENT =
              "<Network number='18' type='Gdn'>"
            + "     <TcpTransport>"
            + "         <IncomingOnly>true</IncomingOnly>"
            + "         <ListeningAddress>1.2.3.4</ListeningAddress>"
            + "         <ListeningPort>3100</ListeningPort>"
            + "         <RemotePort>8000</RemotePort>"
            + "         <ConnectionQueueLimit>500</ConnectionQueueLimit>"
            + "     </TcpTransport>"
            + "     <Protocol name=\"zap\">"
            + "         <MaxPacketSize>2048</MaxPacketSize>"
            + "     </Protocol>"
            + "     <Property name=\"otad.responseTimeoutSeconds\" value=\"300\" />"
            + "</Network>";

    private static final String XML_ELEMENT_WITH_AUTHENTICATION =
            "<Network number='18' type='Gdn' identityType=\"ipAddress\" incomingOnly=\"false\" persistentConnections=\"true\">"
          + "     <InactiveTimeout>05:00:00</InactiveTimeout>"
          + "     <TcpTransport>"
          + "         <IncomingOnly>true</IncomingOnly>"
          + "         <ListeningAddress>1.2.3.4</ListeningAddress>"
          + "         <ListeningPort>3100</ListeningPort>"
          + "         <RemotePort>8000</RemotePort>"
          + "         <ConnectionQueueLimit>500</ConnectionQueueLimit>"
          + "     </TcpTransport>"
          + "     <Protocol name=\"zap\">"
          + "         <MaxPacketSize>2048</MaxPacketSize>"
          + "     </Protocol>"
          + "     <Authentication name='basic'>"
          + "     </Authentication>"
          + "     <Property name=\"otad.responseTimeoutSeconds\" value=\"300\" />"
          + "</Network>";

    @Test
    public void shouldParseElementWithAuthentication() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_ELEMENT_WITH_AUTHENTICATION);
        NetworkConfig networkConfig = NetworkConfig.networkConfigForJdomElement(element);

        assertEquals(IdentityType.IpAddress, networkConfig.getIdentityType());
        assertFalse(networkConfig.isIncomingOnly());
        assertTrue(networkConfig.supportsPersistentConnections());
        
        assertEquals(18000, networkConfig.getInactiveTimeoutSeconds().intValue());
        assertEquals(18, networkConfig.getNetworkNumber().intValue());
        assertEquals("Gdn", networkConfig.getTypeName());
        assertEquals("300", networkConfig.getProperties().get("otad.responseTimeoutSeconds"));
        assertNotNull(networkConfig.getTransportConfig());
        assertTrue(networkConfig.hasAuthentication());
        assertEquals("basic", networkConfig.getAuthenticationConfig().getTypeName());
        assertEquals(TransportType.TCP, networkConfig.getTransportConfig().getTransportType());
        ProtocolConfig protocolConfig = networkConfig.getProtocolConfig();
        assertNotNull(protocolConfig);
        assertEquals("zap", protocolConfig.getName());
        assertEquals(2048, ((ZapProtocolConfig)protocolConfig).getMaxPacketSize().intValue());
    }
    
    @Test
    public void shouldParseElementWithoutAddress() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_ELEMENT);
        NetworkConfig networkConfig = NetworkConfig.networkConfigForJdomElement(element);
        // These test expected default values.        
        assertEquals(IdentityType.SiteProvided, networkConfig.getIdentityType());
        assertTrue(networkConfig.isIncomingOnly());
        assertFalse(networkConfig.supportsPersistentConnections());
        
        assertEquals(0, networkConfig.getInactiveTimeoutSeconds().intValue());
        assertEquals(18, networkConfig.getNetworkNumber().intValue());
        assertEquals("Gdn", networkConfig.getTypeName());
        assertEquals("300", networkConfig.getProperties().get("otad.responseTimeoutSeconds"));
        assertNotNull(networkConfig.getTransportConfig());
        assertFalse(networkConfig.hasAuthentication());
        assertEquals(TransportType.TCP, networkConfig.getTransportConfig().getTransportType());
        ProtocolConfig protocolConfig = networkConfig.getProtocolConfig();
        assertNotNull(protocolConfig);
        assertEquals("zap", protocolConfig.getName());
        assertEquals(2048, ((ZapProtocolConfig)protocolConfig).getMaxPacketSize().intValue());
    }
}
