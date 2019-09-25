package zedi.pacbridge.app.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.app.events.zios.ZiosEventTestCase;
import zedi.pacbridge.net.AlarmStatus;
import zedi.pacbridge.net.DataType;
import zedi.pacbridge.net.ReasonCode;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.values.ZapDataType;

public class SiteReportTest extends ZiosEventTestCase {

    protected static final byte TEST_BLOB[] = new byte[]{(byte)0x93,0x0f,(byte)0xff,(byte)0xee,(byte)0xdb,0x03};
    protected static final byte EXPECTED_BYTES[] = new byte[]{(byte)0x93,0x0f,(byte)0xff,(byte)0xee,(byte)0xdb,0x03};
    protected static final String MESSAGEID = "091634817-22006";
    private static String NUID = "Uncle Wiggly's Hot Shoes Blues Band";
    private static Integer POLLSET_NUMBER = Integer.valueOf(1);
    private static Long TIMESTAMP_SECONDS = 1091634797L;
    private static Date TIMESTAMP = new Date(TIMESTAMP_SECONDS);
    private static ZapReasonCode REASON_CODE = ZapReasonCode.AlarmModify;
    private static Long FLOAT_IOINDEX = 1L;
    private static ZapDataType FLOAT_IOTYPE = ZapDataType.Float;
    private static String FLOAT_IOVALUE = "234.009";
    private static ZapAlarmStatus ALARM_STATUS = ZapAlarmStatus.High;
    private static String IP_ADDRESS = "1.2.3.4";

    protected static SAXBuilder documentBulder = new SAXBuilder();

    private static final String SITE_REPORT_XML =
              "<SiteReport reason=\"Scheduled\" messageId=\"1424731116259\" qualifier=\"ZIOS\">"
            + "    <Nuid>LittleD</Nuid>"
            + "    <PollSetNumber>10</PollSetNumber>"
            + "    <Timestamp>1424799684</Timestamp>"
            + "    <ReportItem index=\"111\" dataType=\"Float\" alarmStatus=\"OK\">"
            + "        <Value>1</Value>"
            + "    </ReportItem>"
            + "    <ReportItem index=\"110\" dataType=\"Discrete\" alarmStatus=\"High\">"
            + "        <Value>1</Value>"
            + "    </ReportItem>"
            + "</SiteReport>";
    
    @Test
    public void shouldBeValidXML() throws Exception {
        assertIsValidXml(SITE_REPORT_XML);
    }

    @Test
    public void shouldWorkWithAllAlarmStati() throws Exception {
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.Low);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.LowLow);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.High);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.HighHigh);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.RTUTimeout);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.RTUError);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.RTUBackoff);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.NoData);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.OutOfRangeLow);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.OutOfRangeHigh);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.RTUOverflow);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.Reserved);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.History);
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.Empty);
    }
    
    @Test
    public void shouldWorkWithAllDataTypes() throws Exception {
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.EmptyValue, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Discrete, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Byte, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.UnsignedByte, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Integer, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.UnsignedInteger, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Long, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.UnsignedLong, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Float, ZapAlarmStatus.OK); 
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Double, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Binary, ZapAlarmStatus.OK);
         assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.String, ZapAlarmStatus.OK);
    }
    
    @Test
    public void shouldWorkWithAllReasonCodes() throws Exception {
        assertWorksWithStuff(ZapReasonCode.Scheduled, ZapDataType.Float, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.DemandPoll, ZapDataType.Float, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.IOModify, ZapDataType.Float, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.IOWrite, ZapDataType.Float, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.AlarmModify, ZapDataType.Float, ZapAlarmStatus.OK);
        assertWorksWithStuff(ZapReasonCode.AlarmTrigger, ZapDataType.Float, ZapAlarmStatus.OK);
    }
    
    @Test
    public void shouldAllowIpAddressInXML() throws Exception {
        SiteReport siteReport = new SiteReport(EventQualifier.ZIOS, NUID, POLLSET_NUMBER, TIMESTAMP, REASON_CODE, EVENT_ID, IP_ADDRESS);
        siteReport.setMessageId(MESSAGEID);
        SiteReportItem item = new SiteReportItem(FLOAT_IOTYPE, FLOAT_IOINDEX, FLOAT_IOVALUE, ALARM_STATUS);
        siteReport.addReportItem(item);
        String xmlString = siteReport.asXmlString();
        Element element = JDomUtilities.elementForXmlString(xmlString);
        assertNotNull(element.getChild(SiteReport.EVENT_ID_TAG));
        assertEquals(EVENT_ID.toString(), element.getChildText(SiteReport.EVENT_ID_TAG));
        assertNotNull(element.getChild(SiteReport.IP_ADDRESS_TAG));
        assertEquals(IP_ADDRESS, element.getChildText(SiteReport.IP_ADDRESS_TAG));
    }
    
    @Test
    public void shouldIncludeEventIdInXML() throws Exception {
        SiteReport siteReport = new SiteReport(EventQualifier.ZIOS, NUID, POLLSET_NUMBER, TIMESTAMP, REASON_CODE, EVENT_ID);
        siteReport.setMessageId(MESSAGEID);
        SiteReportItem item = new SiteReportItem(FLOAT_IOTYPE, FLOAT_IOINDEX, FLOAT_IOVALUE, ALARM_STATUS);
        siteReport.addReportItem(item);
        String xmlString = siteReport.asXmlString();
        Element element = JDomUtilities.elementForXmlString(xmlString);
        assertNotNull(element.getChild(SiteReport.EVENT_ID_TAG));
        assertEquals(EVENT_ID.toString(), element.getChildText(SiteReport.EVENT_ID_TAG));
    }
        
    @Test
    public void shouldGenerateValidXML() throws Exception {
        SiteReport siteReport = new SiteReport(EventQualifier.ZIOS, NUID, POLLSET_NUMBER, TIMESTAMP, REASON_CODE, EVENT_ID);
        siteReport.setMessageId(MESSAGEID);
        SiteReportItem item = new SiteReportItem(FLOAT_IOTYPE, FLOAT_IOINDEX, FLOAT_IOVALUE, ALARM_STATUS);
        siteReport.addReportItem(item);
        String xmlString = siteReport.asXmlString();
        assertIsValidXml(xmlString);
    }
    
    private void assertWorksWithStuff(ReasonCode reasonCode, DataType dataType, AlarmStatus alarmStatu) {
        SiteReport siteReport = new SiteReport(EventQualifier.ZIOS, NUID, POLLSET_NUMBER, TIMESTAMP, REASON_CODE, EVENT_ID);
        siteReport.setMessageId(MESSAGEID);
        SiteReportItem item;
        if (ZapDataType.EmptyValue == dataType)
            item = new SiteReportItem(dataType, FLOAT_IOINDEX, ALARM_STATUS);
        else 
            item = new SiteReportItem(dataType, FLOAT_IOINDEX, FLOAT_IOVALUE, ALARM_STATUS);
        siteReport.addReportItem(item);
        String xmlString = siteReport.asXmlString();
        assertIsValidXml(xmlString);
    }
}
