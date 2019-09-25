package zedi.figdevice.emulator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.jdom2.Element;
import org.junit.Test;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.FixedBundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.RandomBundledReportMessageGenerator;
import zedi.pacbridge.utl.JDomUtilities;

public class ConfigurationTest {

    private static final String FIXED_REPORT_FIXED_INTERVAL_XML_CONFIG = 
              "<FigDevice>"
            + "    <Nuid>TestDevice1</Nuid>"
            + "    <Bridge>"
            + "        <Address>192.168.169.134</Address>"
            + "        <Port>3100</Port>"
            + "    </Bridge>"
            + "    "
            + "    <ReportGenerator>"
            + "        <FixedReport intervalType=\"fixed\">"
            + "            <IntervalSeconds>60</IntervalSeconds>"
            + "            <NumberOfReadings>20</NumberOfReadings>"
            + "        </FixedReport>"
            + "    </ReportGenerator>"
            + "</FigDevice>";

    private static final String FIXED_REPORT_RANDOM_INTERVAL_XML_CONFIG = 
            "<FigDevice>"
          + "    <Nuid>TestDevice1</Nuid>"
          + "    <Bridge>"
          + "        <Address>192.168.169.134</Address>"
          + "        <Port>3100</Port>"
          + "    </Bridge>"
          + "    "
          + "    <ReportGenerator>"
          + "        <FixedReport intervalType=\"random\">"
          + "            <IntervalSeconds>60</IntervalSeconds>"
          + "            <NumberOfReadings>20</NumberOfReadings>"
          + "        </FixedReport>"
          + "    </ReportGenerator>"
          + "</FigDevice>";

    private static final String RANDOM_REPORT_FIXED_INTERVAL_XML_CONFIG = 
            "<FigDevice>"
          + "    <Nuid>TestDevice1</Nuid>"
          + "    <Bridge>"
          + "        <Address>192.168.169.134</Address>"
          + "        <Port>3100</Port>"
          + "    </Bridge>"
          + "    "
          + "    <ReportGenerator>"
          + "        <RandomReport intervalType=\"fixed\">"
          + "            <IntervalSeconds>60</IntervalSeconds>"
          + "            <MaxNumberOfReadings>20</MaxNumberOfReadings>"
          + "            <MinNumberOfReadings>10</MinNumberOfReadings>"
          + "        </RandomReport>"
          + "    </ReportGenerator>"
          + "</FigDevice>";


    @Test
    public void shouldParseRandomReportFixedIntervalConfiguration() throws Exception {
        Element element = JDomUtilities.elementForInputStream(new ByteArrayInputStream(RANDOM_REPORT_FIXED_INTERVAL_XML_CONFIG.getBytes()));
        Configuration configuration = Configuration.configurationFromElement(element);
        
        assertEquals("192.168.169.134", configuration.getBridgeAddress().getHostString());
        assertEquals(3100, configuration.getBridgeAddress().getPort());
        assertTrue( configuration.reportGenerator() instanceof RandomBundledReportMessageGenerator);
    }
    
    @Test
    public void shouldParseFixedReportRandomIntervalConfiguration() throws Exception {
        Element element = JDomUtilities.elementForInputStream(new ByteArrayInputStream(FIXED_REPORT_RANDOM_INTERVAL_XML_CONFIG.getBytes()));
        Configuration configuration = Configuration.configurationFromElement(element);
        
        assertEquals("192.168.169.134", configuration.getBridgeAddress().getHostString());
        assertEquals(3100, configuration.getBridgeAddress().getPort());
        
        assertTrue(configuration.reportGenerator() instanceof FixedBundledReportMessageGenerator);
    }
    
    @Test
    public void shouldParseFixedReportFixedIntervalConfiguration() throws Exception {
        Element element = JDomUtilities.elementForInputStream(new ByteArrayInputStream(FIXED_REPORT_FIXED_INTERVAL_XML_CONFIG.getBytes()));
        Configuration configuration = Configuration.configurationFromElement(element);
        
        assertEquals("192.168.169.134", configuration.getBridgeAddress().getHostString());
        assertEquals(3100, configuration.getBridgeAddress().getPort());
        
        BundledReportMessageGenerator messageGenerator = configuration.reportGenerator();
        assertTrue(messageGenerator instanceof FixedBundledReportMessageGenerator);
    }
    
}
