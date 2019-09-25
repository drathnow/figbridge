package zedi.figbridge.monitor.utl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.text.MessageFormat;

import javax.jms.TextMessage;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ReportMessageListenerTest extends BaseTestCase {
    private static final Integer TIMESTAMP_SECS = 1424799684;
    private static final Long TIMESTAMP_MILLIS = TIMESTAMP_SECS*1000L;
    private static final String USERNAME = "LittleD";
    private static final Integer INDEX = 111;
    
    private static final Integer WRONG_TIMESTAMP_SECS = 98234234;
    private static final Long WRONG_TIMESTAMP_MILLIS= WRONG_TIMESTAMP_SECS * 1000L;
    private static final String WRONG_USERNAME = "Spooge";
    private static final Integer WRONG_INDEX = 666;
    
    
    private static final String SITE_REPORT_XML =
            "<SiteReport reason=\"Scheduled\" messageId=\"1424731116259\" qualifier=\"ZIOS\">"
          + "    <Nuid>{0}</Nuid>"
          + "    <PollSetNumber>10</PollSetNumber>"
          + "    <Timestamp>{1}</Timestamp>"
          + "    <ReportItem index=\"{2}\" dataType=\"Float\" alarmStatus=\"OK\">"
          + "        <Value>1</Value>"
          + "    </ReportItem>"
          + "</SiteReport>";


    @Test
    public void shouldReturnFalseIfWrongIndex() throws Exception {
        String xmlMessage = MessageFormat.format(SITE_REPORT_XML, USERNAME, TIMESTAMP_MILLIS.toString(), WRONG_INDEX.toString());
        TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn(xmlMessage);
        ReportMessageListener listener = new ReportMessageListener(USERNAME, TIMESTAMP_SECS, INDEX);
        listener.onMessage(message);
        assertFalse(listener.isDone());
        assertNull(listener.getLastErrorText());
    }
    
    @Test
    public void shouldReturnFalseIfWrongTimestamp() throws Exception {
        String xmlMessage = MessageFormat.format(SITE_REPORT_XML, USERNAME, WRONG_TIMESTAMP_MILLIS.toString(), INDEX.toString());
        TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn(xmlMessage);
        ReportMessageListener listener = new ReportMessageListener(USERNAME, TIMESTAMP_SECS, INDEX);
        listener.onMessage(message);
        assertFalse(listener.isDone());
        assertNull(listener.getLastErrorText());
    }
    
    @Test
    public void shouldReturnFalseIfWrongUsername() throws Exception {
        String xmlMessage = MessageFormat.format(SITE_REPORT_XML, WRONG_USERNAME, TIMESTAMP_MILLIS.toString(), INDEX.toString());
        TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn(xmlMessage);
        ReportMessageListener listener = new ReportMessageListener(USERNAME, TIMESTAMP_SECS, INDEX);
        listener.onMessage(message);
        assertFalse(listener.isDone());
        assertNull(listener.getLastErrorText());
    }
    
    @Test
    public void shouldReturnTrueIfMessageIsOurs() throws Exception {
        String xmlMessage = MessageFormat.format(SITE_REPORT_XML, USERNAME, TIMESTAMP_MILLIS.toString(), INDEX.toString());
        TextMessage message = mock(TextMessage.class);
        given(message.getText()).willReturn(xmlMessage);
        ReportMessageListener listener = new ReportMessageListener(USERNAME, TIMESTAMP_SECS, INDEX);
        listener.onMessage(message);
        assertTrue(listener.isDone());
        assertNull(listener.getLastErrorText());
    }
}
