package zedi.figbridge.slapper.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class FigDeviceConfigTest extends BaseTestCase {

    private static final String DEFAULT_DEVICE_XML_CONFIG = 
            "<FigDevice count=\"50\">"
          + "    <FixedReport intervalType=\"fixed\">"
          + "        <IntervalSeconds>30</IntervalSeconds>"
          + "        <NumberOfReadings>20</NumberOfReadings>"
          + "    </FixedReport>"
          + "</FigDevice>";

    private static final String DEVICE_WITH_ALL_XML_CONFIG = 
              "<FigDevice count=\"50\" startDelaySeconds=\"30\" reconnectMinutes=\"20\">"
            + "    <FixedReport intervalType=\"fixed\">"
            + "        <IntervalSeconds>30</IntervalSeconds>"
            + "        <NumberOfReadings>20</NumberOfReadings>"
            + "    </FixedReport>"
            + "</FigDevice>";
    
    @Test
    public void shouldParse() throws Exception {
        Element element = JDomUtilities.elementForXmlString(DEVICE_WITH_ALL_XML_CONFIG);
        FigDeviceConfig config = FigDeviceConfig.figDeviceConfigForElement(element);
        assertNotNull(config);
        assertEquals(50, config.getDeviceCount().intValue());
        assertEquals(30, config.getStartDelaySeconds().intValue());
        assertEquals(1200, config.getReconnectSeconds().intValue());
    }

    @Test
    public void shouldParseDefaultValues() throws Exception {
        Element element = JDomUtilities.elementForXmlString(DEFAULT_DEVICE_XML_CONFIG);
        FigDeviceConfig config = FigDeviceConfig.figDeviceConfigForElement(element);
        assertNotNull(config);
        assertEquals(50, config.getDeviceCount().intValue());
        assertEquals(0, config.getStartDelaySeconds().intValue());
        assertEquals(0, config.getReconnectSeconds().intValue());
    }
}
