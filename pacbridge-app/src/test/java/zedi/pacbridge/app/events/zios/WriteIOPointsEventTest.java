package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static zedi.pacbridge.app.util.SiteAddressMatcher.matchesSiteAddress;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jdom2.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.annotations.SampleEventXML;
import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.WriteValueElement;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.net.Value;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;
import zedi.pacbridge.zap.messages.WriteValue;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapFloat;
import zedi.pacbridge.zap.values.ZapValue;


@RunWith(PowerMockRunner.class)
@PrepareForTest({WriteIOPointsEvent.class})
public class WriteIOPointsEventTest extends ZiosEventTestCase {

    public static final String XML_EVENT = 
            "<Event name='WriteIOPoints' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <WriteIOPoints>"
          + "        <WriteValue>"
          + "            <Index>" + INDEX + "</Index>"
          + "            <DataType>" + DATA_TYPE.getName() + "</DataType>"
          + "            <Value>" + VALUE.toString() + "</Value>"
          + "        </WriteValue>"
          + "        <WriteValue>"
          + "            <Index>" + (INDEX+1) + "</Index>"
          + "            <DataType>" + DATA_TYPE.getName() + "</DataType>"
          + "            <Value>" + VALUE.toString() + "</Value>"
          + "        </WriteValue>"
          + "    </WriteIOPoints>"
          + "</Event>";
    
    @Test
    public void shouldHandleEvent() throws Exception {
        Device device = mock(Device.class);
        OutgoingRequestService requestService = mock(OutgoingRequestService.class);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        ControlRequest request = mock(ControlRequest.class);
        DeviceCache deviceCache = mock(DeviceCache.class);
        WriteValue writeValue1 = mock(WriteValue.class);
        WriteValue writeValue2 = mock(WriteValue.class);
        
        whenNew(WriteValue.class)
            .withArguments(eq(INDEX), any(Value.class))
            .thenReturn(writeValue1);
        whenNew(WriteValue.class)
            .withArguments(eq(INDEX+1), any(Value.class))
            .thenReturn(writeValue2);
        whenNew(WriteIoPointsControl.class)
            .withArguments(any(List.class), eq(EVENT_ID))
            .thenReturn(control);
        whenNew(ControlRequest.class)
            .withArguments(any(SiteAddress.class), eq(EVENT_ID), eq(control))
            .thenReturn(request);
        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);

        Element element = JDomUtilities.elementForXmlString(XML_EVENT);
        WriteIOPointsEvent event = WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache);
        event.handle(requestService);
        
        verifyNew(WriteValue.class).withArguments(eq(INDEX), any(Value.class));
        verifyNew(WriteValue.class).withArguments(eq(INDEX+1), any(Value.class));
        verifyNew(WriteIoPointsControl.class).withArguments(any(List.class), eq(EVENT_ID));
        verifyNew(ControlRequest.class).withArguments(any(SiteAddress.class), eq(EVENT_ID), eq(control));
        verify(requestService).queueOutgoingRequest(request);
        verify(deviceCache).deviceForNetworkUnitId(NUID.toString());
    }
    
    @Test
    public void shouldParseEventXMLAndReturnEven() throws Exception {
        ZapDataType dataType = mock(ZapDataType.class);
        ZapValue value = mock(ZapValue.class);
        WriteIOPointsEvent event = mock(WriteIOPointsEvent.class);
        WriteValue newWriteValue1 = mock(WriteValue.class);
        WriteValue newWriteValue2 = mock(WriteValue.class);
        Element element = JDomUtilities.elementForXmlString(XML_EVENT);
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        given(dataType.valueForString(VALUE.toString())).willReturn(value);
        whenNew(WriteValue.class)
            .withArguments(INDEX, value)
            .thenReturn(newWriteValue1);
        whenNew(WriteValue.class)
            .withArguments(INDEX+1, value)
            .thenReturn(newWriteValue2);
        whenNew(WriteIOPointsEvent.class)
            .withArguments(matchesExpecteList(), eq(EVENT_ID), matchesSiteAddress(SITE_ADDRESS), eq(FIRMWARE_VERSION))
            .thenReturn(event);
        
        WriteIOPointsEvent writeIOEvent = WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache);
        assertSame(event, writeIOEvent);

        verifyNew(WriteIOPointsEvent.class).withArguments(matchesExpecteList(), eq(EVENT_ID), matchesSiteAddress(SITE_ADDRESS), eq(FIRMWARE_VERSION));
        verify(deviceCache).deviceForNetworkUnitId(NUID.toString());
        verify(device).getNetworkNumber();
        verify(device).getFirmwareVersion();
    }

    @Test
    public void testAsXmlString() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        assertTrue(isValidXml(XML_EVENT));
        WriteIOPointsEvent setEvent = WriteIOPointsEvent.writeIoPointsEventForElement(JDomUtilities.elementForXmlString(XML_EVENT), deviceCache);
        assertTrue(isValidXml(setEvent.asXmlString()));
    }

    @SampleEventXML
    public static String sampleXml() {
        return XML_EVENT;
    }

    BaseMatcher<List<WriteValueElement>> matchesExpecteList() {

        return new BaseMatcher<List<WriteValueElement>>() {

            String errorMessage;

            @Override
            public boolean matches(Object object) {
                @SuppressWarnings("unchecked")
                List<WriteValueElement> writeValues = (List<WriteValueElement>)object;
                if (writeValues.size() != 2) {
                    errorMessage = "List contains too few elements. Expected 2 but was " + writeValues.size();
                    return false;
                }

                WriteValueElement writeValue = writeValues.get(0);
                if (writeValue.getIndex().intValue() != INDEX.intValue()) {
                    errorMessage = "Wrong IO ID in first WriteValue. Expected " + (INDEX + 1) + " but was " + writeValue.getIndex();
                    return false;
                }
                if (writeValue.getValue().dataType().equals(DATA_TYPE) == false) {
                    errorMessage = "Wrong data type in first WriteValue. Expected " + DATA_TYPE.getName() + " but was " + writeValue.getValue().dataType().getName();
                    return false;
                }

                ZapFloat floatValue = (ZapFloat)writeValue.getValue();
                if (floatValue.getValue().floatValue() != VALUE.floatValue()) {
                    errorMessage = "Wrong value in first WriteValue. Expected " + VALUE.toString() + " but was " + writeValue.getValue().toString();
                    return false;
                }

                writeValue = writeValues.get(1);
                if (writeValue.getIndex().intValue() != INDEX.intValue() + 1) {
                    errorMessage = "Wrong IO ID in second WriteValue. Expected " + (INDEX + 1) + " but was " + writeValue.getIndex();
                    return false;
                }
                if (writeValue.getValue().dataType().equals(DATA_TYPE) == false) {
                    errorMessage = "Wrong data type in second WriteValue. Expected " + DATA_TYPE.getName() + " but was " + writeValue.getValue().dataType().getName();
                    return false;
                }
                floatValue = (ZapFloat)writeValue.getValue();
                if (floatValue.getValue().floatValue() != VALUE.floatValue()) {
                    errorMessage = "Wrong value in second WriteValue. Expected " + VALUE.toString() + " but was " + writeValue.getValue().toString();
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(errorMessage);
            }
        };
    }
}
