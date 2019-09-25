package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static zedi.pacbridge.app.util.SiteAddressMatcher.matchesSiteAddress;

import java.util.List;

import org.jdom2.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldDataType;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigureEvent.class})
public class ConfigureEventTest extends ZiosEventTestCase {

    private static final String XML_EVENT = 
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

    private static final String DEVICE_CONFIGURE_XML =
              "<?xml version='1.0' encoding='utf-8'?>"
            + "<Event name='Configure' qualifier='ZIOS'>"
            + "    <EventId>10650411</EventId>"
            + "    <Nuid>BigD</Nuid>"
            + "    <Configure object='device'>"
            + "        <Action type='add'>"
            + "            <Id>3</Id>"
            + "            <CorrelationId>117428</CorrelationId>"
            + "            <Name>COM1-RTUSIM01</Name>"
            + "            <PortId>1</PortId>"
            + "            <ProtocolId>1</ProtocolId>"
            + "            <MaxRetries>1</MaxRetries>"
            + "            <RequestTimeout>1000</RequestTimeout>"
            + "            <RtuBackoffCount>3</RtuBackoffCount>"
            + "            <RtuBackoffTimeout>180000</RtuBackoffTimeout>"
            + "            <Parameters>1:1</Parameters>"
            + "        </Action>"
            + "    </Configure>"
            + "</Event>";            
        
    @Test
    public void shouldValidateDeviceXML() throws Exception {
        assertIsValidXml(DEVICE_CONFIGURE_XML);
    }

    @Test
    @Ignore
    public void shouldShouldParseXmlEvent() throws Exception {
        ConfigureEvent configureEvent = mock(ConfigureEvent.class);
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);
        
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        Element element = JDomUtilities.elementForXmlString(XML_EVENT);
        
        FieldType corIdType = mock(FieldType.class); 
        FieldType idType = mock(FieldType.class); 
        FieldType nameType = mock(FieldType.class); 
        
        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        given(corIdType.getDataType()).willReturn(FieldDataType.S16);
        given(idType.getDataType()).willReturn(FieldDataType.S16);
        given(nameType.getDataType()).willReturn(FieldDataType.STRING);
        given(fieldTypeLibrary.fieldTypeForName("CorrelationId")).willReturn(corIdType);
        given(fieldTypeLibrary.fieldTypeForName("Id")).willReturn(idType);
        given(fieldTypeLibrary.fieldTypeForName("Name")).willReturn(nameType);

        whenNew(ConfigureEvent.class)
            .withArguments(eq(EVENT_ID), matchesSiteAddress(SITE_ADDRESS), eq(FIRMWARE_VERSION), eq(ObjectType.SITE), arg.capture())
            .thenReturn(configureEvent);
        
        ConfigureEvent event = ConfigureEvent.configureEventForElement(element, fieldTypeLibrary, deviceCache);
        verifyNew(ConfigureEvent.class)
            .withArguments(eq(EVENT_ID), matchesSiteAddress(SITE_ADDRESS), eq(FIRMWARE_VERSION), eq(ObjectType.SITE), arg.capture());
        
        assertSame(configureEvent, event);

        List<Action> actions = arg.getValue();
        
        Action action = actions.get(0);
        assertEquals(ActionType.DELETE, action.getActionType());
        List<Field<?>> fields = action.getFields();
        assertEquals(corIdType, fields.get(0).getFieldType());
        assertEquals(123, ((Long)fields.get(0).getValue()).intValue());
        assertEquals(idType, fields.get(1).getFieldType());
        assertEquals(5, ((Long)fields.get(1).getValue()).intValue());
        
        action = actions.get(1);
        assertEquals(ActionType.DELETE, action.getActionType());
        fields = action.getFields();
        assertEquals(corIdType, fields.get(0).getFieldType());
        assertEquals(567, ((Long)fields.get(0).getValue()).intValue());
        assertEquals(idType, fields.get(1).getFieldType());
        assertEquals(21, ((Long)fields.get(1).getValue()).intValue());

        action = actions.get(2);
        assertEquals(ActionType.ADD, action.getActionType());
        fields = action.getFields();
        assertEquals(corIdType, fields.get(0).getFieldType());
        assertEquals(123, ((Long)fields.get(0).getValue()).intValue());
        assertEquals(nameType, fields.get(1).getFieldType());
        assertEquals("Freddy Zipplemier", (String)fields.get(1).getValue());

        action = actions.get(3);
        assertEquals(ActionType.UPDATE, action.getActionType());
        fields = action.getFields();
        assertEquals(corIdType, fields.get(0).getFieldType());
        assertEquals(123, ((Long)fields.get(0).getValue()).intValue());
        assertEquals(idType, fields.get(1).getFieldType());
        assertEquals(456, ((Long)fields.get(1).getValue()).intValue());
        assertEquals(nameType, fields.get(2).getFieldType());
        assertEquals("Archie Bunker", (String)fields.get(2).getValue());
    }
    
    @Test
    public void testAsXmlString() throws Exception {
        assertTrue(isValidXml(XML_EVENT));
    }
  
}
