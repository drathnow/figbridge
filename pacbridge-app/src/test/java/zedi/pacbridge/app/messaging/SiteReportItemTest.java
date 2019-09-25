package zedi.pacbridge.app.messaging;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.jdom2.Element;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.values.ZapDataType;


public class SiteReportItemTest {

    private static final String VALUE = "50.0";
    private static final Long INDEX = 1L;
    public static final String ITEM_STRING = 
        "<reportItem index='1' dataType='Float' alarmStatus='OK'>"
        + "<value>50.0</value>" 
        + "</reportItem>";

    @Test
    public void testToXMLString() throws Exception {
        SiteReportItem item = new SiteReportItem(ZapDataType.Float, INDEX, VALUE, ZapAlarmStatus.OK);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlPullParserFactory factory;
        factory = XmlPullParserFactory.newInstance();
        XmlSerializer serializer = factory.newSerializer();
        serializer.setOutput(new PrintWriter(output));
        
        item.serialize(serializer);
        serializer.endDocument();
        String xmlString = new String(output.toByteArray());
        
        Element rootElement = JDomUtilities.elementForXmlString(xmlString);
        assertEquals(SiteReportItem.TAG_REPORT_ITEM, rootElement.getName());
        assertEquals(INDEX.longValue(), Long.parseLong(rootElement.getAttributeValue(SiteReportItem.TAG_INDEX)));
        assertEquals(ZapDataType.Float.getName(), rootElement.getAttributeValue(SiteReportItem.TAG_DATA_TYPE));
        assertEquals(ZapAlarmStatus.OK.getName(), rootElement.getAttributeValue(SiteReportItem.TAG_ALARM_STATUS));
        assertEquals(VALUE, rootElement.getChildText(SiteReportItem.TAG_VALUE));
    }
}
