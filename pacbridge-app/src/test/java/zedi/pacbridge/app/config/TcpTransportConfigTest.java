package zedi.pacbridge.app.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.Test;

import zedi.pacbridge.utl.JDomUtilities;

public class TcpTransportConfigTest {

    private static final String BOTH_XML = 
              "<TcpTransport incomingOnly='false'>"
            + "    <IncomingOnly>false</IncomingOnly>"
            + "    <ListeningPort>3100</ListeningPort>"
            + "    <RemotePort>8000</RemotePort>"
            + "    <MaxConnectionAttempts>4</MaxConnectionAttempts>"
            + "    <ConnectionQueueLimit>500</ConnectionQueueLimit>"
            + "</TcpTransport>";
    
    
    private static final String INCOMING_ONLY_WITH_ADDRESS_XML = 
              "<TcpTransport incomingOnly='true'>"
            + "     <ListeningAddress>1.2.3.4</ListeningAddress>"
            + "     <ListeningPort>3100</ListeningPort>"
            + "     <ConnectionQueueLimit>500</ConnectionQueueLimit>"
            + "</TcpTransport>";

    private static final String INCOMING_ONLY_XML = 
              "<TcpTransport>"
            + "     <ListeningPort>3100</ListeningPort>"
            + "     <ConnectionQueueLimit>500</ConnectionQueueLimit>"
            + "</TcpTransport>";
    
    @Test
    public void shouldParseWithIncomingAddress() throws Exception {
        Element element = JDomUtilities.elementForXmlString(INCOMING_ONLY_WITH_ADDRESS_XML);
        TcpTransportConfig transportConfig = TcpTransportConfig.transportConfigForElement(element);
        assertTrue(transportConfig.isIncomingOnly());
        assertEquals(3100, transportConfig.getListeningPort().intValue());
        assertEquals(500, transportConfig.getConnectionQueueLimit().intValue());
        assertEquals("1.2.3.4", transportConfig.getListeningAddress());
    }
    
    @Test
    public void shouldParseIncomingOnly() throws Exception {
        Element element = JDomUtilities.elementForXmlString(INCOMING_ONLY_XML);
        TcpTransportConfig transportConfig = TcpTransportConfig.transportConfigForElement(element);
        assertTrue(transportConfig.isIncomingOnly());
        assertEquals(3100, transportConfig.getListeningPort().intValue());
        assertEquals(500, transportConfig.getConnectionQueueLimit().intValue());
        assertNull(transportConfig.getListeningAddress());
    }
    
    @Test
    public void shouldParseElementThatAllowsIncomingAndOutgoing() throws JDOMException, IOException {
        Element element = JDomUtilities.elementForXmlString(BOTH_XML);
        TcpTransportConfig transportConfig = TcpTransportConfig.transportConfigForElement(element);
        assertFalse(transportConfig.isIncomingOnly());
        assertEquals(3100, transportConfig.getListeningPort().intValue());
        assertEquals(8000, transportConfig.getRemotePort().intValue());
        assertEquals(4, transportConfig.getMaxConnectionAttempts().intValue());
        assertEquals(500, transportConfig.getConnectionQueueLimit().intValue());
        assertNull(transportConfig.getListeningAddress());
    }
}
