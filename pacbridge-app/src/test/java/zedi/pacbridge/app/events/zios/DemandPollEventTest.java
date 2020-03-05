package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static zedi.pacbridge.app.util.SiteAddressMatcher.matchesSiteAddress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.annotations.SampleEventXML;
import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.DemandPollControl;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DemandPollEvent.class})
public class DemandPollEventTest extends ZiosEventTestCase {
    
    public static final String XML_EVENT = 
            "<Event name='DemandPoll' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <FirmwareVersion>" + FIRMWARE_VERSION + "</FirmwareVersion>"
          + "    <DemandPoll>"
          + "        <Index>" + INDEX + "</Index>"
          + "        <PollsetNumber>" + POLLSET_NUMBER + "</PollsetNumber>"
          + "    </DemandPoll>"
          + "</Event>";
    
    @Test
    public void shouldCreateCorrectXmlStringWithConstructor() throws Exception {
        DemandPollEvent event = new DemandPollEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, INDEX, POLLSET_NUMBER);
        System.out.println(event.asXmlString());
        assertIsValidXml(event.asXmlString());
    }
    
    @Test
    public void shouldCallControlEventHandler() throws Exception {
        ControlRequest controlRequest = mock(ControlRequest.class);
        OutgoingRequestService controlService = mock(OutgoingRequestService.class);
        DemandPollControl control = mock(DemandPollControl.class);
        
        ArgumentCaptor<ControlRequest> arg = ArgumentCaptor.forClass(ControlRequest.class);

        whenNew(DemandPollControl.class)
            .withArguments(EVENT_ID, INDEX, POLLSET_NUMBER)
            .thenReturn(control);
        whenNew(ControlRequest.class)
            .withArguments(SITE_ADDRESS, EVENT_ID, control)
            .thenReturn(controlRequest);

        DemandPollEvent demandPollEvent = new DemandPollEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, INDEX, POLLSET_NUMBER);
        demandPollEvent.handle(controlService);

        verify(controlService).queueOutgoingRequest(arg.capture());
        
        verifyNew(DemandPollControl.class).withArguments(EVENT_ID, INDEX, POLLSET_NUMBER);
        verifyNew(ControlRequest.class).withArguments(SITE_ADDRESS, EVENT_ID, control);
    }
    
    @Test
    public void shouldParseEventXML() throws Exception {
        DemandPollEvent event = mock(DemandPollEvent.class);
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        whenNew(DemandPollEvent.class)
            .withArguments(eq(EVENT_ID), argThat(matchesSiteAddress(SITE_ADDRESS)), eq(FIRMWARE_VERSION), eq(INDEX), eq(POLLSET_NUMBER))
            .thenReturn(event);

        assertIsValidXml(XML_EVENT);
        DemandPollEvent demandPollEvent = DemandPollEvent.demandPollEventEventForElement(JDomUtilities.elementForXmlString(XML_EVENT), deviceCache);
        verifyNew(DemandPollEvent.class).withArguments(eq(EVENT_ID), argThat(matchesSiteAddress(SITE_ADDRESS)), eq(FIRMWARE_VERSION), eq(INDEX), eq(POLLSET_NUMBER));
        assertSame(event, demandPollEvent);
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

        assertIsValidXml(XML_EVENT);
        DemandPollEvent demandPollEvent = DemandPollEvent.demandPollEventEventForElement(JDomUtilities.elementForXmlString(XML_EVENT), deviceCache);
        assertIsValidXml(demandPollEvent.asXmlString());
    }
    
    @SampleEventXML
    public static String sampleXml() {
        return XML_EVENT;
    }
}
