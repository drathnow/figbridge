package zedi.figbridge.monitor.utl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ConfigurationTest extends BaseTestCase {

    private static final String XML_CONFIG = 
                 "<FigBridgeMonitor>"
            +    "    <Property name=\"foo\" value=\"bar\" />"
            +    "    <Jms>"
            +    "        <QueueManagerName>QM_csmqdev1</QueueManagerName>"
            +    "        <HostName>csmqdev1</HostName>"
            +    "        <EventTopicName>topic://scada/system/events</EventTopicName>"
            +    "    </Jms>"
            +    "    <Bridge>"
            +    "        <Address>192.168.169.64</Address>"
            +    "        <Port>3100</Port>"
            +    "    </Bridge>"
            +    "    <Authentication>"
            +    "        <Nuid>SpoogeMcFee</Nuid>"
            +    "        <SecretKey>ABCDEFGHIJKLMNOPQRSTUVWXYZ==</SecretKey>"
            +    "    </Authentication>"
            +    "</FigBridgeMonitor>";
    
    
    @Test
    public void shouldParseConfiguration() throws Exception {
        Configuration configuration = new Configuration(XML_CONFIG);
        assertEquals(System.getProperty("foo"), "bar");
        assertEquals("QM_csmqdev1", configuration.getJmsQueueManagerName());
        assertEquals("csmqdev1", configuration.getJmsHostName());
        assertEquals("topic://scada/system/events", configuration.getJmsEventTopicName());
        assertEquals("192.168.169.64", configuration.getBridgeAddress());
        assertEquals(3100, configuration.getBridgePortNumber().intValue());
        assertEquals("SpoogeMcFee", configuration.getNuid());
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ==", configuration.getBase64SecretKey());
    }
}
