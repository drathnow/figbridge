package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.jdom2.Element;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ObjectType;
import zedi.pacbridge.zap.values.ZapDataType;

public class ConfigureBigEventTest extends BaseTestCase {
    public static Element bigEventElement;
    public static Element smallEventElement;
    
    private static final String STRING_64_BYTES = RandomStringUtils.randomAlphanumeric(64); 
    private static final String STRING_128_BYTES = RandomStringUtils.randomAlphanumeric(128); 
    
    private static final String FULL_ADD_IO_EVENT =
              "<Event name=\"Configure\" qualifier=\"ZIOS\">"
            + "<EventId>" + Long.MAX_VALUE + "</EventId>"
            + "<Nuid>" + STRING_128_BYTES + "</Nuid>"
            + "<Configure object=\"ioPoint\">"
            + "  <Action type=\"add\">"
            + "    <IOPointClass>6</IOPointClass>"
            + "    <DataType>" + ZapDataType.LONG_LONG + "</DataType>"
            + "    <PollSetId>1</PollSetId>"
            + "    <ExternalDeviceId>0</ExternalDeviceId>"
            + "    <Tag>" + STRING_128_BYTES + "</Tag>"
            + "    <SiteId>" + Short.MAX_VALUE + "</SiteId>"
            + "    <SourceAddress>" + STRING_64_BYTES + "</SourceAddress>"
            + "    <SensorClassName>" + STRING_128_BYTES + "</SensorClassName>"
            + "    <IsReadOnly>1</IsReadOnly>"
            + "    <AlarmMask>16</AlarmMask>"
            + "    <AlarmSetHysteresis>" + Long.MAX_VALUE + "</AlarmSetHysteresis>"
            + "    <AlarmClearHysteresis>" + Long.MAX_VALUE + "</AlarmClearHysteresis>"
            + "    <LowLowSet>" + Long.MAX_VALUE + "</LowLowSet>"
            + "    <LowLowHysteresis>" + Long.MAX_VALUE + "</LowLowHysteresis>"
            + "    <LowSet>" + Long.MAX_VALUE + "</LowSet>"
            + "    <LowHysteresis>" + Long.MAX_VALUE + "</LowHysteresis>"
            + "    <HighHighSet>" + Long.MAX_VALUE + "</HighHighSet>"
            + "    <HighHighHysteresis>" + Long.MAX_VALUE + "</HighHighHysteresis>"
            + "    <HighSet>" + Long.MAX_VALUE + "</HighSet>"
            + "    <HighHysteresis>" + Long.MAX_VALUE + "</HighHysteresis>"
            + "    <CorrelationId>" + Long.MAX_VALUE + "</CorrelationId>"
            + "  </Action>"
            + "</Configure>"
            + "</Event>";
    @Mock
    private DeviceCache deviceCache;
    
    private ZiosFieldTypeLibrary fieldTypeLibrary = new ZiosFieldTypeLibrary();
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        InputStream inputStream = ConfigureBigEventTest.class.getResourceAsStream("/zedi/pacbridge/app/events/examples/zios/BigConfigureEvent.xml");
        bigEventElement = JDomUtilities.elementForInputStream(inputStream);
        inputStream = ConfigureBigEventTest.class.getResourceAsStream("/zedi/pacbridge/app/events/examples/zios/SmallConfigureEvent.xml");
        smallEventElement = JDomUtilities.elementForInputStream(inputStream);

        given(deviceCache.deviceForNetworkUnitId(anyString())).willReturn(null);
        fieldTypeLibrary.loadFieldTypes();
    }
    

    @Test
    public void shouldCaculateSizesWithBigTagName() throws Exception {
        ConfigureControl emptyControl = new ConfigureControl(Long.MAX_VALUE, ObjectType.IO_POINT, new ArrayList<Action>());
        System.out.println("Size of empty control: " + emptyControl.size());
        System.out.println("XML: " + FULL_ADD_IO_EVENT);
        Element element = JDomUtilities.elementForXmlString(FULL_ADD_IO_EVENT);
        ConfigureEvent event = ConfigureEvent.configureEventForElement(element, fieldTypeLibrary, deviceCache);
        ConfigureControl control = new ConfigureControl(event.getEventId(), event.getObjectType(), event.getActions());
        System.out.println("Event size: " + control.size());
        Action action = new Action(ActionType.ADD, event.getActions().get(0).getFields());
        System.out.println("Action size: " + action.size());
    }
    
    @Test
    public void shouldCalculateSize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        ConfigureEvent event = ConfigureEvent.configureEventForElement(smallEventElement, fieldTypeLibrary, deviceCache);
        ConfigureControl control = new ConfigureControl(event.getEventId(), event.getObjectType(), event.getActions());
        control.serialize(byteBuffer);        
        System.out.println("Small Calculated size: " + control.size());
        System.out.println("Small Serialized size: " + byteBuffer.position());
        assertEquals(control.size().intValue(), byteBuffer.position());
    }
    
    @Test
    public void shouldCreateEvent() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        DeviceCache deviceCache = mock(DeviceCache.class);
        given(deviceCache.deviceForNetworkUnitId(anyString())).willReturn(null);
        ZiosFieldTypeLibrary fieldTypeLibrary = new ZiosFieldTypeLibrary();
        fieldTypeLibrary.loadFieldTypes();
        ConfigureEvent event = ConfigureEvent.configureEventForElement(bigEventElement, fieldTypeLibrary, deviceCache);
        ConfigureControl control = new ConfigureControl(event.getEventId(), event.getObjectType(), event.getActions());
        control.serialize(byteBuffer);
        System.out.println("Big Calculated size: " + control.size());
        System.out.println("Big Serialized size: " + byteBuffer.position());
        assertEquals(control.size().intValue(), byteBuffer.position());
}
}