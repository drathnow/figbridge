package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureUpdateEventTest extends ZiosEventTestCase {

    private static final SiteAddress siteAddress = new NuidSiteAddress(NUID);
    
    private static String XML_EVENT = "<Event name=\"ConfigureUpdate\" qualifier=\"ZIOS\">"
                                    + "<EventId/>"
                                    + "<Nuid>BigD</Nuid>"
                                    + "<ConfigureUpdate object=\"device\">"
                                    + "  <Action type=\"add\">"
                                    + "    <Id>8</Id>"
                                    + "    <Name>New Device</Name>"
                                    + "    <PortId>1</PortId>"
                                    + "    <Parameters>1:1</Parameters>"
                                    + "    <ProtocolId>1</ProtocolId>"
                                    + "    <MaxRetries>1</MaxRetries>"
                                    + "    <RequestTimeout>2000</RequestTimeout>"
                                    + "    <RtuBackoffCount>0</RtuBackoffCount>"
                                    + "    <RtuBackoffTimeout>0</RtuBackoffTimeout>"
                                    + "  </Action>"
                                    + "</ConfigureUpdate>"
                                    + "</Event>";
    
    @Test
    public void shouldValidate() throws Exception {
        assertIsValidXml(XML_EVENT);
    }
    
    @Test
    public void shouldCreateXmlString() throws Exception {
        List<Action> actions = new ArrayList<>();
        List<Field<?>> fields = new ArrayList<>();
        Action deleteAction = new Action(ActionType.DELETE, fields);
        Action addAction =  new Action(ActionType.ADD, fields);
        Action updateAction =  new Action(ActionType.UPDATE, fields);
                
        actions.add(deleteAction);
        actions.add(addAction);
        actions.add(updateAction);
        
        ConfigureUpdateEvent event = new ConfigureUpdateEvent(siteAddress, ObjectType.DEVICE, actions);
        String xmlString = event.asXmlString();
        assertNotNull(xmlString);
        assertIsValidXml(xmlString);
        
        Element rootElement = JDomUtilities.elementForXmlString(xmlString);
        
        assertEquals(ZiosEvent.ROOT_ELEMENT_NAME, rootElement.getName());
        assertNotNull(rootElement.getAttribute(Event.NAME_TAG));
        assertEquals(ConfigureUpdateEvent.ROOT_ELEMENT_NAME, rootElement.getAttributeValue(Event.NAME_TAG));
        assertNotNull(rootElement.getAttribute(Event.QUALIFIER_TAG));
        assertEquals(EventQualifier.ZIOS.getName(), rootElement.getAttribute(Event.QUALIFIER_TAG).getValue());

        Element configeUpdateElement = rootElement.getChild(ConfigureUpdateEvent.ROOT_ELEMENT_NAME);
        assertNotNull(configeUpdateElement);
        assertEquals(ObjectType.DEVICE.getName(), configeUpdateElement.getAttributeValue(ConfigureUpdateEvent.OBJECT_TAG));
        
        List<Element> elements = configeUpdateElement.getChildren(Action.ROOT_ELEMENT_NAME);
        assertNotNull(elements);
        assertEquals(3, elements.size());
        
        
    }
}
