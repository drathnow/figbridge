package zedi.pacbridge.app.controls.zap;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.OtadRequestAckDetails;
import zedi.pacbridge.zap.messages.OtadRequestControl;
import zedi.pacbridge.zap.messages.ProtocolErrorDetails;
import zedi.pacbridge.zap.messages.ProtocolErrorType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OtadRequestControlResponseStrategy.class, ZiosEventResponseEvent.class})
public class OtadRequestControlResponseStrategyTest extends BaseTestCase {
    private static final String ADDRESS = "DavesNotHere";
    private static final Integer SEQUENCE_NUMBER = 42;
    private static final Long COMMAND_ID = 543L;
    
    @Mock
    private SiteAddress siteAddress;

    @Mock
    private EventHandler eventPublisher;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        given(siteAddress.getAddress()).willReturn(ADDRESS);
    }
    
    @Test
    public void shouldHandleProtocolError() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        OtadRequestControl control = mock(OtadRequestControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        ProtocolErrorDetails details = new ProtocolErrorDetails(ProtocolErrorType.InvalidMessageNumber);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.isProtocolError()).willReturn(true);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.OtadRequest);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.getEventId()).willReturn(COMMAND_ID);
        given(control.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.messageType()).willReturn(ZapMessageType.OtadRequest);
        
        whenNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), any(String.class), eq(null)).thenReturn(event);
        
        OtadRequestControlResponseStrategy strategy = new OtadRequestControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        strategy.completeProcessing();

        verify(eventPublisher).publishEvent(event);
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        verifyNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS),any(String.class), eq(null));
    }
    
    @Test
    public void shouldHandleFailedAck() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        OtadRequestControl control = mock(OtadRequestControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        OtadRequestAckDetails details = mock(OtadRequestAckDetails.class);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.OtadRequest);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(details.getStatusMessage()).willReturn(OtadRequestAckDetails.FAILURE);
        given(details.isSuccessful()).willReturn(false);
        given(control.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.messageType()).willReturn(ZapMessageType.OtadRequest);
        given(control.getEventId()).willReturn(COMMAND_ID);
        
        whenNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS),any(String.class), eq(null)).thenReturn(event);
        
        OtadRequestControlResponseStrategy strategy = new OtadRequestControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        strategy.completeProcessing();

        verify(eventPublisher).publishEvent(event);
        verifyNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS),any(String.class), eq(null));
    }
    
    @Test
    public void shouldHandleSuccessfullAck() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        OtadRequestControl control = mock(OtadRequestControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        OtadRequestAckDetails details = mock(OtadRequestAckDetails.class);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.OtadRequest);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(details.getStatusMessage()).willReturn(OtadRequestAckDetails.SUCCESS);
        given(details.isSuccessful()).willReturn(true);
        given(control.messageType()).willReturn(ZapMessageType.OtadRequest);
        given(control.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.getEventId()).willReturn(COMMAND_ID);

        whenNew(ZiosEventResponseEvent.class).withArguments(COMMAND_ID, EventStatus.Processing, ADDRESS).thenReturn(event);
        
        OtadRequestControlResponseStrategy strategy = new OtadRequestControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());

        verify(eventPublisher).publishEvent(event);
        verifyNew(ZiosEventResponseEvent.class).withArguments(COMMAND_ID, EventStatus.Processing, ADDRESS);
    }
    
}
