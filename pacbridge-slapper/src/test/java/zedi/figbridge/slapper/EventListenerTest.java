package zedi.figbridge.slapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.TextMessage;

import org.junit.Test;

import zedi.figbridge.slapper.utl.DeviceConglomerator;
import zedi.pacbridge.test.BaseTestCase;

public class EventListenerTest extends BaseTestCase {
    private static final String XML_EVENT = "Message: <SiteReport reason=\"Scheduled\" qualifier=\"ZIOS\" messageId=\"zi0w-ts08.server-one-1432576616008\"><EventId>123</EventId><Nuid>LittleD</Nuid><PollSetNumber>1</PollSetNumber><Timestamp>1432577915000</Timestamp><ReportItem index=\"2\" dataType=\"UnsignedByte\" alarmStatus=\"OK\"><Value>0</Value></ReportItem><ReportItem index=\"6\" dataType=\"UnsignedShort\" alarmStatus=\"High\"><Value>16384</Value></ReportItem><ReportItem index=\"7\" dataType=\"Double\" alarmStatus=\"OK\"><Value>2.03125</Value></ReportItem><ReportItem index=\"9\" dataType=\"Discrete\" alarmStatus=\"RtuOverflow\"><Value>1</Value></ReportItem><ReportItem index=\"10\" dataType=\"Byte\" alarmStatus=\"OutOfRangeLow\"><Value>-83</Value></ReportItem><ReportItem index=\"13\" dataType=\"Double\" alarmStatus=\"RtuError\"><Value>200879672824877120000000000000000000000000000000000000000000</Value></ReportItem><ReportItem index=\"14\" dataType=\"UnsignedShort\" alarmStatus=\"OK\"><Value>64</Value></ReportItem><ReportItem index=\"15\" dataType=\"Byte\"><Value>-73</Value></ReportItem><ReportItem index=\"16\" dataType=\"Float\" alarmStatus=\"HighHigh\"><Value /></ReportItem><ReportItem index=\"19\" dataType=\"Short\" alarmStatus=\"reserved\"><Value /></ReportItem><ReportItem index=\"20\" dataType=\"Short\" alarmStatus=\"RtuTimeout\"><Value>-6080</Value></ReportItem></SiteReport>";

    @Test
    public void shouldFoo() throws Exception {
        Pattern eventIdPattern = Pattern.compile(EventListener.THE_RE);
        
        Matcher matcher = eventIdPattern.matcher(XML_EVENT);
        assertTrue(matcher.matches());
        assertEquals("123", matcher.group(1));
        assertEquals("LittleD", matcher.group(2));
    }
    
    @Test
    public void shouldParseEvent() throws Exception {
        DeviceConglomerator conglomerator = mock(DeviceConglomerator.class);
        TextMessage message = mock(TextMessage.class);
        
        given(message.getText()).willReturn(XML_EVENT);
        
        EventListener eventListener = new EventListener(conglomerator);
        eventListener.onMessage(message);
        
        verify(conglomerator).removeEventIdForDeviceName(123L, "LittleD");
    }
}
