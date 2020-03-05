package zedi.pacbridge.app.zap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.EventData;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.OtadStatus;
import zedi.pacbridge.zap.messages.OtadStatusMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OtadStatusMessageHandler.class)
public class OtadStatusMessageHandlerTest extends BaseTestCase {
    private static final String ADDRESS = "FOO";
    private static final Long EVENT_ID = 1234L;
    private static final String TEN_PERCENT = "10";
    private static final String FAILURE_MSG = "It's bad!";
    
    @Mock
    private EventHandler publisher;
    
    @Test
    public void shouldFoo() throws Exception {
        EventData eventData = new EventData();
        eventData.addProperty("Step", "Failed");
        eventData.addProperty("Message", "It's bad");

        ZiosEventResponseEvent event = new ZiosEventResponseEvent(EVENT_ID, 
                EventStatus.Failure, 
                ADDRESS, 
                ZiosEventName.OtadRequest, 
                OtadStatus.FAILED.getName() + " - " + FAILURE_MSG,
                eventData);
    }
    
    @Test
    public void shouldHandleOtadStatusMessageForFailureStatus() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OtadStatusMessage statusMessage = mock(OtadStatusMessage.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        EventData eventData = mock(EventData.class);
        
        given(statusMessage.getEventId()).willReturn(EVENT_ID);
        given(statusMessage.getOtadStatusType()).willReturn(OtadStatus.FAILED);
        given(statusMessage.getOptionalData()).willReturn(FAILURE_MSG);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        
        whenNew(EventData.class)
            .withNoArguments()
            .thenReturn(eventData);
        
        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, 
                           EventStatus.Failure, 
                           ADDRESS, 
                           ZiosEventName.OtadRequest, 
                           OtadStatus.FAILED.getName() + " - " + FAILURE_MSG,
                           eventData)
            .thenReturn(event);

        OtadStatusMessageHandler handler = new OtadStatusMessageHandler(publisher);
        assertTrue(handler.didProcessStatusUpdateMessage(siteAddress, statusMessage));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, 
                                                              EventStatus.Failure, 
                                                              ADDRESS, 
                                                              ZiosEventName.OtadRequest, 
                                                              OtadStatus.FAILED.getName() + " - " + FAILURE_MSG,
                                                              eventData);

        verify(publisher).publishEvent(event);
        verify(eventData).addProperty("Step", OtadStatus.FAILED.getName());
    }

    
    @Test
    public void shouldHandleOtadStatusMessageForDownloading() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OtadStatusMessage statusMessage = mock(OtadStatusMessage.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        EventData eventData = mock(EventData.class);
        
        given(statusMessage.getEventId()).willReturn(EVENT_ID);
        given(statusMessage.getOtadStatusType()).willReturn(OtadStatus.DOWNLOADING);
        given(statusMessage.getOptionalData()).willReturn(TEN_PERCENT);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        
        whenNew(EventData.class)
            .withNoArguments()
            .thenReturn(eventData);
        
        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, 
                           EventStatus.Processing, 
                           ADDRESS, 
                           ZiosEventName.OtadRequest, 
                           OtadStatus.DOWNLOADING.getName() + " 10%",
                           eventData)
            .thenReturn(event);

        OtadStatusMessageHandler handler = new OtadStatusMessageHandler(publisher);
        assertTrue(handler.didProcessStatusUpdateMessage(siteAddress, statusMessage));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, 
                                                              EventStatus.Processing, 
                                                              ADDRESS, 
                                                              ZiosEventName.OtadRequest, 
                                                              OtadStatus.DOWNLOADING.getName() + " 10%",
                                                              eventData);

        verify(publisher).publishEvent(event);
        verify(eventData).addProperty("Step", OtadStatus.DOWNLOADING.getName());
        verify(eventData).addProperty("Percent", TEN_PERCENT);
    }
    
    @Test
    public void shouldReturnFalseIfPublisherThrowsException() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OtadStatusMessage statusMessage = mock(OtadStatusMessage.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        EventData eventData = mock(EventData.class);
        
        given(statusMessage.getEventId()).willReturn(EVENT_ID);
        given(statusMessage.getOtadStatusType()).willReturn(OtadStatus.INSTALLING);
        given(statusMessage.getOptionalData()).willReturn(null);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        
        whenNew(EventData.class)
            .withNoArguments()
            .thenReturn(eventData);

        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, 
                           EventStatus.Processing, 
                           ADDRESS, 
                           ZiosEventName.OtadRequest, 
                           OtadStatus.INSTALLING.getName(),
                           eventData)
            .thenReturn(event);

        doThrow(RuntimeException.class).when(publisher).publishEvent(event);
        
        OtadStatusMessageHandler handler = new OtadStatusMessageHandler(publisher);
        assertFalse(handler.didProcessStatusUpdateMessage(siteAddress, statusMessage));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, 
                                                             EventStatus.Processing, 
                                                             ADDRESS, 
                                                             ZiosEventName.OtadRequest, 
                                                             OtadStatus.INSTALLING.getName(),
                                                             eventData);
        verify(publisher).publishEvent(event);
    }
    
    @Test
    public void shouldProcessStatusUpdateMessage() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        OtadStatusMessage statusMessage = mock(OtadStatusMessage.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        EventData eventData = mock(EventData.class);

        given(statusMessage.getEventId()).willReturn(EVENT_ID);
        given(statusMessage.getOtadStatusType()).willReturn(OtadStatus.INSTALLING);
        given(statusMessage.getOptionalData()).willReturn(TEN_PERCENT);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        
        whenNew(EventData.class)
            .withNoArguments()
            .thenReturn(eventData);

        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, 
                           EventStatus.Processing, 
                           ADDRESS, 
                           ZiosEventName.OtadRequest, 
                           OtadStatus.INSTALLING.getName(),
                           eventData)
            .thenReturn(event);
        
        OtadStatusMessageHandler handler = new OtadStatusMessageHandler(publisher);
        assertTrue(handler.didProcessStatusUpdateMessage(siteAddress, statusMessage));
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, 
                                                             EventStatus.Processing, 
                                                             ADDRESS, 
                                                             ZiosEventName.OtadRequest, 
                                                             OtadStatus.INSTALLING.getName(),
                                                             eventData);
        verify(publisher).publishEvent(event);
        verify(eventData).addProperty("Step", OtadStatus.INSTALLING.getName());
    }

}
