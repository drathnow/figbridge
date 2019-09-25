package zedi.figbridge.slapper.config;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class ConfigurationTest extends BaseTestCase {
    private static final String TEST_NAME = "TestBridge";
    private static final String XML_CONFIG = 
                      "<BridgeSlapper>"
                    + "    <Jms>"
                    + "        <QueueManagerName>QM_csmqdev1</QueueManagerName>"
                    + "        <HostName>csmqdev1</HostName>"
                    + "        <ClientId>pacbridge_id</ClientId>"
                    + "        <RawDataDestinationName>topic://scada/system/events</RawDataDestinationName>"
                    + "    </Jms>"
                    + ""
                    + "    <Property name=\"eventIdTracker.deadScanIntervalMinutes\" value=\"2\" />"
                    + ""
                    + "    <Bridge>"
                    + "        <Address>192.168.169.134</Address>"
                    + "        <Port>3100</Port>"
                    + "    </Bridge>"
                    + "    <FigDevice count=\"50\">"
                    + "        <FixedReport intervalType=\"fixed\">"
                    + "            <IntervalSeconds>30</IntervalSeconds>"
                    + "            <NumberOfReadings>20</NumberOfReadings>"
                    + "        </FixedReport>"
                    + "    </FigDevice>"
                    + "    <FigDevice count=\"50\">"
                    + "        <FixedReport intervalType=\"random\">"
                    + "            <IntervalSeconds>30</IntervalSeconds>"
                    + "            <NumberOfReadings>20</NumberOfReadings>"
                    + "        </FixedReport>"
                    + "    </FigDevice>"
                    + "    <FigDevice count=\"50\">"
                    + "        <RandomReport intervalType=\"fixed\">"
                    + "            <IntervalSeconds>60</IntervalSeconds>"
                    + "            <MinNumberOfReadings>10</MinNumberOfReadings>"
                    + "            <MaxNumberOfReadings>50</MaxNumberOfReadings>"
                    + "        </RandomReport>"
                    + "    </FigDevice>"
                    + "    <FigDevice count=\"50\">"
                    + "        <RandomReport intervalType=\"random\">"
                    + "            <IntervalSeconds>60</IntervalSeconds>"
                    + "            <MinNumberOfReadings>10</MinNumberOfReadings>"
                    + "            <MaxNumberOfReadings>50</MaxNumberOfReadings>"
                    + "        </RandomReport>"
                    + "    </FigDevice>"
                    + "</BridgeSlapper>";

    @Test
    public void shouldParseConfigurationAndSetSystemProperties() throws Exception {
        Properties properties = new Properties();
        Configuration configuration = Configuration.configurationFromElement(JDomUtilities.elementForXmlString(XML_CONFIG));
        configuration.setPropertiesInProperties(properties);
        assertEquals("2", properties.getProperty("eventIdTracker.deadScanIntervalMinutes"));
    }
    
    @Test
    public void shouldParseConfiguration() throws Exception {
        Configuration configuration = Configuration.configurationFromElement(JDomUtilities.elementForXmlString(XML_CONFIG));
        assertEquals(Configuration.DEFAULT_NAME, configuration.getName());
        assertEquals(4, configuration.getDeviceConfigs().size());
        assertEquals("192.168.169.134", configuration.getBridgeAddress().getHostString());
        assertEquals(3100, configuration.getBridgeAddress().getPort());
        assertEquals("QM_csmqdev1", configuration.getJmsQueueManagerName());
        assertEquals("csmqdev1", configuration.getJmsHostName());
        assertEquals("pacbridge_id", configuration.getJmsClientId());
        assertEquals("topic://scada/system/events", configuration.getRawDataTopic());
    }
    
    @Test
    public void shouldParseConfigurationWithName() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG);
        element.setAttribute(Configuration.NAME_TAG, TEST_NAME);
        Configuration configuration = Configuration.configurationFromElement(element);
        assertEquals(TEST_NAME, configuration.getName());
        assertEquals(4, configuration.getDeviceConfigs().size());
        assertEquals("192.168.169.134", configuration.getBridgeAddress().getHostString());
        assertEquals(3100, configuration.getBridgeAddress().getPort());
        assertEquals("QM_csmqdev1", configuration.getJmsQueueManagerName());
        assertEquals("csmqdev1", configuration.getJmsHostName());
        assertEquals("pacbridge_id", configuration.getJmsClientId());
        assertEquals("topic://scada/system/events", configuration.getRawDataTopic());
    }

}
