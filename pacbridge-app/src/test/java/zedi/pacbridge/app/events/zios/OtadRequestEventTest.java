package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jdom2.Element;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.OtadRequestControl;

public class OtadRequestEventTest extends ZiosEventTestCase {

    private static final String URL = "http://foo.manchoo.com/hello/world.zip";
    private static final String MD5_HASH = "0123456789ABCDEF";
    private static final Integer RETRIES = 3;
    private static final Integer RETRY_INTERVAL = 30;
    private static final Integer TIMEOUT = 300;
    
    public static final String XML_FULL_EVENT = 
            "<Event name='OtadRequest' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <FirmwareVersion>" + FIRMWARE_VERSION + "</FirmwareVersion>"
          + "    <OtadRequest>"
          + "        <Url>" + URL + "</Url>"
          + "        <ForceRestart>true</ForceRestart>"
          + "        <UseAuthentication>true</UseAuthentication>"
          + "        <Retries>" + RETRIES + "</Retries>"
          + "        <TimeoutSeconds>" + TIMEOUT + "</TimeoutSeconds>"
          + "        <Md5Hash>" + MD5_HASH + "</Md5Hash>"
          + "    </OtadRequest>"
          + "</Event>";

    public static final String XML_NOAUTH_NOFORCE_EVENT = 
            "<Event name='OtadRequest' qualifier='ZIOS'>"
          + "    <EventId>" + EVENT_ID + "</EventId>"
          + "    <Nuid>" + NUID + "</Nuid>"
          + "    <NetworkNumber>" + NETWORK_NUMBER + "</NetworkNumber>"
          + "    <FirmwareVersion>" + FIRMWARE_VERSION + "</FirmwareVersion>"
          + "    <OtadRequest>"
          + "        <Url>" + URL + "</Url>"
          + "        <Retries>" + RETRIES + "</Retries>"
          + "        <TimeoutSeconds>" + TIMEOUT + "</TimeoutSeconds>"
          + "        <Md5Hash>" + MD5_HASH + "</Md5Hash>"
          + "    </OtadRequest>"
          + "</Event>";

    
    @Test
    public void shouldAssignFalseToUseAuthenticationIfElementIsMissing() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID)).willReturn(null);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_NOAUTH_NOFORCE_EVENT);
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertFalse(event.isUseAuthentication());
        assertFalse(event.isForceRestart());
    }
    
    @Test
    public void shouldSendControlFromDefaultXml() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        OutgoingRequestService service = mock(OutgoingRequestService.class);
        
        given(deviceCache.deviceForNetworkUnitId(NUID)).willReturn(null);
        
        ArgumentCaptor<ControlRequest> arg = ArgumentCaptor.forClass(ControlRequest.class);
        Element eventElement = JDomUtilities.elementForXmlString(XML_NOAUTH_NOFORCE_EVENT);
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        event.handle(service);
        
        verify(service).queueOutgoingRequest(arg.capture());
        assertEquals(ControlRequest.class, arg.getValue().getClass());
        assertEquals(OtadRequestControl.class, ((ControlRequest)arg.getValue()).getControl().getClass());
        OtadRequestControl control = (OtadRequestControl)((ControlRequest)arg.getValue()).getControl();
        
        assertEquals(EVENT_ID, control.getEventId());
        assertEquals(RETRIES, control.getRetries());
        assertEquals(0, control.getRetryIntervalSeconds().intValue());
        assertEquals(TIMEOUT, control.getTimeoutSeconds());
        assertEquals(URL, control.getOtadFileUrl());
        assertEquals(MD5_HASH, control.getMd5Hash());
        assertFalse(control.getFlags().isForceRestartEnabled());
        assertFalse(control.getFlags().isUseAuthentication());
    }
    
    @Test
    public void shouldHandleWithOutgoingRequestServiceWithForceRestartFalse() throws Exception {
        OutgoingRequestService service = mock(OutgoingRequestService.class);
        
        ArgumentCaptor<ControlRequest> arg = ArgumentCaptor.forClass(ControlRequest.class);
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, false, true, RETRIES, RETRY_INTERVAL, TIMEOUT);
        event.handle(service);
        
        verify(service).queueOutgoingRequest(arg.capture());
        assertEquals(ControlRequest.class, arg.getValue().getClass());
        assertEquals(OtadRequestControl.class, ((ControlRequest)arg.getValue()).getControl().getClass());
        OtadRequestControl control = (OtadRequestControl)((ControlRequest)arg.getValue()).getControl();
        
        assertEquals(EVENT_ID, control.getEventId());
        assertEquals(RETRIES, control.getRetries());
        assertEquals(TIMEOUT, control.getTimeoutSeconds());
        assertEquals(URL, control.getOtadFileUrl());
        assertEquals(MD5_HASH, control.getMd5Hash());
        assertFalse(control.getFlags().isForceRestartEnabled());
        assertTrue(control.getFlags().isUseAuthentication());
    }
    
    @Test
    public void shouldHandleWithOutgoingRequestServiceWithForceFlagsSet1() throws Exception {
        OutgoingRequestService service = mock(OutgoingRequestService.class);
        
        ArgumentCaptor<ControlRequest> arg = ArgumentCaptor.forClass(ControlRequest.class);
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, true, true, RETRIES, RETRY_INTERVAL, TIMEOUT);
        event.handle(service);
        
        verify(service).queueOutgoingRequest(arg.capture());
        assertEquals(ControlRequest.class, arg.getValue().getClass());
        assertEquals(OtadRequestControl.class, ((ControlRequest)arg.getValue()).getControl().getClass());
        OtadRequestControl control = (OtadRequestControl)((ControlRequest)arg.getValue()).getControl();
        
        assertEquals(EVENT_ID, control.getEventId());
        assertEquals(RETRIES, control.getRetries());
        assertEquals(TIMEOUT, control.getTimeoutSeconds());
        assertEquals(URL, control.getOtadFileUrl());
        assertEquals(MD5_HASH, control.getMd5Hash());
        assertTrue(control.getFlags().isForceRestartEnabled());
        assertTrue(control.getFlags().isUseAuthentication());
    }

    @Test
    public void shouldHandleWithOutgoingRequestServiceWithForceFlagsSet2() throws Exception {
        OutgoingRequestService service = mock(OutgoingRequestService.class);
        
        ArgumentCaptor<ControlRequest> arg = ArgumentCaptor.forClass(ControlRequest.class);
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, false, false, RETRIES, RETRY_INTERVAL, TIMEOUT);
        event.handle(service);
        
        verify(service).queueOutgoingRequest(arg.capture());
        assertEquals(ControlRequest.class, arg.getValue().getClass());
        assertEquals(OtadRequestControl.class, ((ControlRequest)arg.getValue()).getControl().getClass());
        OtadRequestControl control = (OtadRequestControl)((ControlRequest)arg.getValue()).getControl();
        
        assertEquals(EVENT_ID, control.getEventId());
        assertEquals(RETRIES, control.getRetries());
        assertEquals(TIMEOUT, control.getTimeoutSeconds());
        assertEquals(URL, control.getOtadFileUrl());
        assertEquals(MD5_HASH, control.getMd5Hash());
        assertFalse(control.getFlags().isForceRestartEnabled());
        assertFalse(control.getFlags().isUseAuthentication());
    }

    @Test
    public void shouldFormatValidXmlWithFullEvent() throws Exception {
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, false, true, RETRIES, 0, TIMEOUT);
        assertIsValidXml(event.asXmlString());
    }
    
    @Test
    public void shouldFormatValidXmlWithNoRetries() throws Exception {
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, true, true, 0, 0, TIMEOUT);
        assertIsValidXml(event.asXmlString());
    }

    @Test
    public void shouldFormatValidXmlWithNoTimeout() throws Exception {
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, false, true, RETRIES, 0, 0);
        assertIsValidXml(event.asXmlString());
    }

    @Test
    public void shouldFormatValidXmlWithNoRetriesOrTimeout() throws Exception {
        OtadRequestEvent event = new OtadRequestEvent(EVENT_ID, SITE_ADDRESS, FIRMWARE_VERSION, URL, MD5_HASH, false, true, 0, 0, 0);
        assertIsValidXml(event.asXmlString());
    }

    @Test
    public void shouldParseFullElement() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(RETRIES, event.getRetries());
        assertEquals(TIMEOUT, event.getTimeoutSeconds());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
        assertTrue(event.isForceRestart());
        assertTrue(event.isUseAuthentication());
    }

    @Test
    public void shouldParseElementOmittedForceRestart() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        //
        // Remove the ForceRestart and UseAuthentication 
        //
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.FORCE_RESTART_TAG);
        eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.USE_AUTHENTICAION_TAG);
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(RETRIES, event.getRetries());
        assertEquals(TIMEOUT, event.getTimeoutSeconds());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
        assertFalse(event.isForceRestart());
        assertFalse(event.isUseAuthentication());
    }

    @Test
    public void shouldParseElementFalseForceRestart() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        eventElement.getChild(ZiosEventName.OtadRequest.getName())
                    .getChild(OtadRequestEvent.FORCE_RESTART_TAG)
                    .setText("false");
        eventElement.getChild(ZiosEventName.OtadRequest.getName())
                    .getChild(OtadRequestEvent.USE_AUTHENTICAION_TAG)
                    .setText("false");
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(RETRIES, event.getRetries());
        assertEquals(TIMEOUT, event.getTimeoutSeconds());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
        assertFalse(event.isForceRestart());
        assertFalse(event.isUseAuthentication());
    }

    @Test
    public void shouldParseElementWithoutRetries() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        assertTrue(eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.RETRIES_TAG));
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(0, event.getRetries().intValue());
        assertEquals(TIMEOUT, event.getTimeoutSeconds());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
    }
    
    @Test
    public void shouldParseElementWithRetryInterval() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        eventElement.getChild(ZiosEventName.OtadRequest.getName()).addContent(new Element(OtadRequestEvent.RETRY_INTERVAL_TAG).setText(RETRY_INTERVAL.toString()));
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(RETRIES.intValue(), event.getRetries().intValue());
        assertEquals(0, event.getRetryIntervalSeconds().intValue());
        assertEquals(TIMEOUT, event.getTimeoutSeconds());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
    }

    @Test
    public void shouldParseElementWithoutTimeout() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        assertTrue(eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.TIMEOUT_TAG));
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(RETRIES, event.getRetries());
        assertEquals(0, event.getTimeoutSeconds().intValue());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
    }

    @Test
    public void shouldParseElementWithoutRetriesOrTimeout() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        Device device = mock(Device.class);

        given(deviceCache.deviceForNetworkUnitId(NUID.toString())).willReturn(device);
        given(device.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(device.getFirmwareVersion()).willReturn(FIRMWARE_VERSION);
        
        Element eventElement = JDomUtilities.elementForXmlString(XML_FULL_EVENT);
        assertTrue(eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.RETRIES_TAG));
        assertTrue(eventElement.getChild(ZiosEventName.OtadRequest.getName()).removeChild(OtadRequestEvent.TIMEOUT_TAG));
        OtadRequestEvent event = OtadRequestEvent.otadRequestEventForElement(eventElement, deviceCache);
        
        assertNotNull(event);
        assertEquals(ZiosEventName.OtadRequest, event.getEventName());
        assertEquals(EventQualifier.ZIOS, event.getEventQualifier());
        assertEquals(EVENT_ID, event.getEventId());
        assertEquals(0, event.getRetries().intValue());
        assertEquals(0, event.getTimeoutSeconds().intValue());
        assertEquals(URL, event.getUrl());
        assertEquals(MD5_HASH, event.getMd5Hash());
    }

}
