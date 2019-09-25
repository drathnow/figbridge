package zedi.pacbridge.app.config;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.zap.ZapProtocolConfig;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.GlobalScheduledExecutor;

public class XmlBridgeConfigurationTest extends BaseTestCase {

    private static final String XML_CONFIG = 
                      "<?xml version='1.0' encoding='UTF-8'?>"
                    + "<PacBridge name=\"bridge1\">"
                    + "    <Properties>"
                    + "        <Property name=\"prop1\" value=\"foo\" />"
                    + "        <Property name=\"prop2\" value=\"${prop1}.bar\" />"
                    + "        <Property name='foo' value='bar' />"                   
                    + "    </Properties>"
                    + "    <Networks>"
                    + "        <Network number='18' type='Gdn'>"
                    + "            <ApplicationLayer name=\"gdn\"/>"
                    + "            <TcpTransport incomingOnly = 'true'>"
                    + "                <ListeningAddress>1.2.3.4</ListeningAddress>"
                    + "                <ListeningPort>3100</ListeningPort>"
                    + "                <ConnectionQueueLimit>500</ConnectionQueueLimit>"
                    + "            </TcpTransport>"
                    + "            <Protocol name='zap'>"
                    + "                <MaxPacketSize>2048</MaxPacketSize>"
                    + "            </Protocol>"
                    + "            <Property name='otad.responseTimeoutSeconds' value='300' />"
                    + "        </Network>"
                    + "    </Networks>"
                    + "    <ControlExclusionAddresses>"
                    + "        <Address ip='1.2.3.4' number='17' />"
                    + "    </ControlExclusionAddresses>"
                    + "    <FieldTypes>"
                    + "        <FieldType tag='4' type='s32'>ErrorCode</FieldType>"
                    + "    </FieldTypes>"
                    + "</PacBridge>";
    
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
    
    
    @Test
    public void shouldDoFullParse() throws Exception {
        XmlBridgeConfiguration bridgeConfiguration = new XmlBridgeConfiguration();
        bridgeConfiguration.loadConfigFromStream(new ByteArrayInputStream(XML_CONFIG.getBytes()));
        
        assertEquals(1, bridgeConfiguration.getNetworkConfigurations().size());
        NetworkConfig networkConfig = bridgeConfiguration.getNetworkConfigurations().get(0);
        assertEquals(18, networkConfig.getNetworkNumber().intValue());
        assertEquals("Gdn", networkConfig.getTypeName());
        
        TransportConfig transportConfig = networkConfig.getTransportConfig();
        assertEquals("1.2.3.4", transportConfig.getListeningAddress());
        assertEquals(3100, transportConfig.getListeningPort().intValue());
        assertEquals(500, transportConfig.getConnectionQueueLimit().intValue());
        
        ProtocolConfig protocolConfig = networkConfig.getProtocolConfig();
        assertEquals("zap", protocolConfig.getName());
        assertEquals(2048, ((ZapProtocolConfig)protocolConfig).getMaxPacketSize().intValue());
        
        assertEquals("foo", System.getProperty("prop1"));
        assertEquals("foo.bar", System.getProperty("prop2"));
        
        assertEquals(1, bridgeConfiguration.getFieldTypes().size());
    }
    
    @Test
    public void shouldLoadFromStream() throws Exception {
        XmlBridgeConfiguration bridgeConfiguration = new XmlBridgeConfiguration(scheduledExecutor);
        bridgeConfiguration.loadConfigFromStream(new ByteArrayInputStream(XML_CONFIG.getBytes()));
        assertEquals("bridge1", bridgeConfiguration.getBridgeName());
        assertEquals(1, bridgeConfiguration.getNetworkConfigurations().size());
        assertEquals(1, bridgeConfiguration.addressExclusionList().size());
        assertEquals("bar", System.getProperty("foo"));
    }

}
