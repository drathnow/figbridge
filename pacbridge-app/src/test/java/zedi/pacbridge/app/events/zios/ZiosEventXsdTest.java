package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ZiosEventXsdTest extends ZiosEventTestCase {

    private static final String VALID_XML_EVENT1 = 
            "<Event name='Configure' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <Configure object='site'>"
          + "        <Action type='delete'>"
          + "            <Id>5</Id>"
          + "        </Action>"
          + "    </Configure>"
          + "</Event>";
    
    private static final String VALID_XML_EVENT2 = 
            "<Event name='Configure' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <Configure object='site'>"
          + "        <Action type='delete'>"
          + "            <CorrelationId>123</CorrelationId>"
          + "            <Id>5</Id>"
          + "        </Action>"
          + "        <Action type='delete'>"
          + "            <CorrelationId>567</CorrelationId>"
          + "            <Id>21</Id>"
          + "        </Action>"
          + "        <Action type='add'>"
          + "            <CorrelationId>123</CorrelationId>"
          + "            <Name>Freddy Zipplemier</Name>"
          + "        </Action>"
          + "        <Action type='update'>"
          + "            <CorrelationId>123</CorrelationId>"
          + "            <Id>456</Id>"
          + "            <Name>Archie Bunker</Name>"
          + "        </Action>"
          + "    </Configure>"
          + "</Event>";

    
    @Test
    public void shouldErrorWhenActionDoesNotContainCorrelationId() throws Exception {
        assertTrue(isValidXml(VALID_XML_EVENT1));
    }

    @Test
    public void shouldValidConfigureEvent() throws Exception {
        assertTrue(xmlValidationException(), isValidXml(VALID_XML_EVENT2));
    }
}
