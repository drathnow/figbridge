package zedi.pacbridge.app.controls.zap;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventName;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.ScrubControl;
import zedi.pacbridge.zap.messages.ScrubControlAckDetails;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ScrubControlResponseStrategy.class, ZiosEventResponseEvent.class})
public class ScrubControlResponseStrategyTest extends BaseTestCase {
    private static final Long EVENT_ID = 1234L;
    private static String ADDRESS = "FooManChoo";

    @Test
    public void shouldHandleAckMessage() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ScrubControl control = mock(ScrubControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        EventHandler eventPublisher = mock(EventHandler.class);
        AckMessage message = mock(AckMessage.class);
        ScrubControlAckDetails details = mock(ScrubControlAckDetails.class);
        ZiosEventResponseEvent eventResponse = mock(ZiosEventResponseEvent.class);
        
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        given(control.getEventId()).willReturn(EVENT_ID);
        given(control.messageType()).willReturn(ZapMessageType.Scrub);
        given(message.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(message.getAckedMessageType()).willReturn(ZapMessageType.Scrub);
        given(message.isProtocolError()).willReturn(false);
        given(message.additionalDetails()).willReturn(details);
        given(details.isSuccessful()).willReturn(true);
        given(details.getStatusMessage()).willReturn("Success");
        
        whenNew(ZiosEventResponseEvent.class)
            .withArguments(EVENT_ID, EventStatus.Success, ADDRESS, ZiosEventName.Scrub)
            .thenReturn(eventResponse);
        
        ScrubControlResponseStrategy strategy = new ScrubControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.handleMessage(message);
        
        verify(siteAddress).getAddress();
        verify(control).getEventId();
        verify(control).messageType();
        verify(message).messageType();
        verify(message).getAckedMessageType();
        verify(message).isProtocolError();
        verify(message).additionalDetails();
        verify(details).isSuccessful();
        verify(details).getStatusMessage();
        
        verifyNew(ZiosEventResponseEvent.class).withArguments(EVENT_ID, EventStatus.Success, ADDRESS, ZiosEventName.Scrub);
        verify(eventPublisher).publishEvent(eventResponse);
    }
}
