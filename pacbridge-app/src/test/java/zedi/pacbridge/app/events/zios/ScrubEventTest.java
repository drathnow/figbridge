package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.zap.messages.ScrubControl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ScrubEvent.class, 
                 NuidSiteAddress.class})
public class ScrubEventTest extends ZiosEventTestCase {
    private static final Long EVENT_ID = 12345L;
    private static final String NUID = "ZED-00001";
    private static final String XML_EVENT = 
              "<Event name='Scrub' qualifier='ZIOS'>"
            + "   <EventId>" + EVENT_ID.toString() + "</EventId>"
            + "   <Nuid>" + NUID + "</Nuid>"
            + "   <Scrub>"
            + "       <IOPoints/>"
            + "       <Reports/>"
            + "       <Events/>"
            + "       <All/>"
            + "   </Scrub>"
            + "</Event>";
    
    
    @Test
    public void shouldBuildControlAndPassToHandler() throws Exception {
        OutgoingRequestService requestSevice = mock(OutgoingRequestService.class);
        ScrubControl control = mock(ScrubControl.class);
        NuidSiteAddress siteAddress = new NuidSiteAddress(NUID, 17);
        ControlRequest controlRequest = mock(ControlRequest.class);
        
        int expectedOptions = ScrubControl.MSG_SCRUB_IO_POINTS;

        whenNew(ScrubControl.class)
            .withArguments(EVENT_ID, expectedOptions)
            .thenReturn(control);
        whenNew(ControlRequest.class)
            .withArguments(siteAddress, EVENT_ID, control)
            .thenReturn(controlRequest);
        
        ScrubEvent event = new ScrubEvent(EVENT_ID, siteAddress, null, true, false, false, false);
        
        event.handle(requestSevice);
        
        verifyNew(ScrubControl.class).withArguments(EVENT_ID, expectedOptions);
        verifyNew(ControlRequest.class).withArguments(siteAddress, EVENT_ID, control);
        verify(requestSevice).queueOutgoingRequest(controlRequest);
    }
    
    @Test
    public void shouldParseElement() throws Exception {
        DeviceCache deviceCache = mock(DeviceCache.class);
        ScrubEvent event = mock(ScrubEvent.class);
        NuidSiteAddress siteAddress = mock(NuidSiteAddress.class);
        Element element = JDomUtilities.elementForXmlString(XML_EVENT);
        
        whenNew(NuidSiteAddress.class)
            .withArguments(NUID, 17).
            thenReturn(siteAddress);
        whenNew(ScrubEvent.class)
            .withArguments(EVENT_ID, siteAddress, null, true, true, true, true)
            .thenReturn(event);
        ScrubEvent result = ScrubEvent.scrubEventForElement(element, deviceCache);

        assertSame(event, result);
        verifyNew(NuidSiteAddress.class).withArguments(NUID, 17);
        verifyNew(ScrubEvent.class).withArguments(EVENT_ID, siteAddress, null, true, true, true, true);

    }
    
    @Test
    public void shouldBeValidXML() throws Exception {
        assertIsValidXml(XML_EVENT);
        assertIsValidXml(xmlStringAfterRemoving("All"));
        assertIsValidXml(xmlStringAfterRemoving("IOPoints"));
        assertIsValidXml(xmlStringAfterRemoving("Events"));
        assertIsValidXml(xmlStringAfterRemoving("Reports"));
    }
    
    private String xmlStringAfterRemoving(String elementName) throws JDOMException, IOException {
        Element element = JDomUtilities.elementForXmlString(XML_EVENT);
        element.getChild(ZiosEventName.Scrub.getName()).removeChild(elementName);
        return JDomUtilities.xmlStringForElement(element);
    }
}
