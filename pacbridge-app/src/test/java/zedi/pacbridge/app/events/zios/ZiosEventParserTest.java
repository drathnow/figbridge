package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.jdom2.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DemandPollEvent.class, 
                 WriteIOPointsEvent.class,
                 DemandPollEvent.class,
                 ZiosEventName.class,
                 ConfigureEvent.class})
public class ZiosEventParserTest extends BaseTestCase {

    @Mock
    private DeviceCache deviceCache;

    @Test
    public void shouldSupportConfigureEvent() throws Exception {
        Element element = mock(Element.class);
        ConfigureEvent event = mock(ConfigureEvent.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        
        mockStatic(ConfigureEvent.class);
        mockStatic(ZiosEventName.class);

        when(element.getAttributeValue("name")).thenReturn(ZiosEventName.Configure.getName());
        when(ConfigureEvent.configureEventForElement(element, fieldTypeLibrary, deviceCache)).thenReturn(event);
        when(ZiosEventName.eventNameForName(ZiosEventName.Configure.getName())).thenReturn(ZiosEventName.Configure);
        
        ZiosEventFactory parser = new ZiosEventFactory(fieldTypeLibrary, deviceCache);
        assertSame(event, parser.eventForElement(element));
    }
    
    @Test
    public void shouldSupportDemandPoll() throws Exception {
        Element element = mock(Element.class);
        DemandPollEvent event = mock(DemandPollEvent.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        
        mockStatic(DemandPollEvent.class);
        mockStatic(ZiosEventName.class);
        
        when(element.getAttributeValue("name")).thenReturn(ZiosEventName.DemandPoll.getName());
        when(DemandPollEvent.demandPollEventEventForElement(element, deviceCache)).thenReturn(event);
        when(ZiosEventName.eventNameForName(ZiosEventName.DemandPoll.getName())).thenReturn(ZiosEventName.DemandPoll);
        
        ZiosEventFactory parser = new ZiosEventFactory(fieldTypeLibrary, deviceCache);
        parser.eventForElement(element);
        
        verify(element).getAttributeValue("name");
    }
    
    @Test
    public void shouldSupportWriteIOPointsEvent() throws Exception {
        Element element = mock(Element.class);
        WriteIOPointsEvent event = mock(WriteIOPointsEvent.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        
        mockStatic(WriteIOPointsEvent.class);
        mockStatic(ZiosEventName.class);
        
        when(element.getAttributeValue("name")).thenReturn(ZiosEventName.WriteIOPoints.getName());
        when(WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache)).thenReturn(event);
        when(ZiosEventName.eventNameForName(ZiosEventName.WriteIOPoints.getName())).thenReturn(ZiosEventName.WriteIOPoints);
        
        ZiosEventFactory parser = new ZiosEventFactory(fieldTypeLibrary, deviceCache);
        assertSame(event, parser.eventForElement(element));
        verify(element).getAttributeValue("name");
    }
}
