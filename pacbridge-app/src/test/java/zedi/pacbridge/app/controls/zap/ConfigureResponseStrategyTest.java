package zedi.pacbridge.app.controls.zap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ConfigureResponseEvent;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckDetails;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigureResponseStrategy.class, 
                 ConfigureResponseEvent.class, 
                 ZiosEventResponseEvent.class})
public class ConfigureResponseStrategyTest extends BaseTestCase {

    private static final Integer SEQ_NUM = 100;
    private static final Long COMMAND_ID = 200L;
    private static final String MSG = "FOO";
    private static final String NUID = "Spooge";
    
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private EventHandler eventPublisher;
    
    @Override
    public void setUp() throws Exception {
       super.setUp();
       given(siteAddress.getAddress()).willReturn(NUID);
    }

    @Test
    public void shouldPublishErrorIfProtocolError() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ConfigureControl control = mock(ConfigureControl.class);
        AckDetails details = mock(AckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        
        given(control.getEventId()).willReturn(COMMAND_ID);
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.Configure);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NUM);
        given(ackMessage.isProtocolError()).willReturn(true);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(details.toString()).willReturn(MSG);
        given(control.messageType()).willReturn(ZapMessageType.Configure);
        given(control.sequenceNumber()).willReturn(SEQ_NUM);
        
        whenNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(NUID), any(String.class), eq(null)).thenReturn(event);
        
        ConfigureResponseStrategy strategy = new ConfigureResponseStrategy(control, fieldTypeLibrary, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        
        strategy.completeProcessing();
        verifyNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(NUID), any(String.class), eq(null));
        verify(eventPublisher).publishEvent(event);
    }
    
    @Test
    @Ignore
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void shouldPublishConfigureResponseEventMessage() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ConfigureControl control = mock(ConfigureControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        ConfigureResponseAckDetails details = mock(ConfigureResponseAckDetails.class);
        ConfigureResponseEvent event = mock(ConfigureResponseEvent.class);

        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        
        List<Action> actions = new ArrayList<Action>();
        actions.add(action1);
        actions.add(action2);
        
        whenNew(ConfigureResponseEvent.class).withArguments(eq(ObjectType.SITE), eq(COMMAND_ID), any(List.class), eq(NUID)).thenReturn(event);
        
        given(details.getEventId()).willReturn(COMMAND_ID);
        given(details.getObjectType()).willReturn(ObjectType.SITE);
        given(details.actionsUsingFieldTypeLibarary(fieldTypeLibrary)).willReturn(actions);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.Configure);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NUM);
        given(ackMessage.isProtocolError()).willReturn(false);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(control.messageType()).willReturn(ZapMessageType.Configure);
        given(control.sequenceNumber()).willReturn(SEQ_NUM);

        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);

        ConfigureResponseStrategy strategy = new ConfigureResponseStrategy(control, fieldTypeLibrary, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        
        strategy.completeProcessing();
        
        verify(eventPublisher).publishEvent(event);
        verify(details).actionsUsingFieldTypeLibarary(fieldTypeLibrary);
        verifyNew(ConfigureResponseEvent.class).withArguments(eq(ObjectType.SITE), eq(COMMAND_ID), arg.capture(), eq(NUID));
        List<Action> list = arg.getValue();
        
        assertEquals(2, list.size());
        assertTrue(list.contains(action1));
        assertTrue(list.contains(action2));
    }
    
    @Test
    public void shouldProcessAckMessageWithResponseDetails() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ConfigureControl control = mock(ConfigureControl.class);
        ConfigureResponseAckDetails details = mock(ConfigureResponseAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.Configure);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NUM);
        given(ackMessage.isProtocolError()).willReturn(false);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(control.messageType()).willReturn(ZapMessageType.Configure);
        given(control.sequenceNumber()).willReturn(SEQ_NUM);
        
        ConfigureResponseStrategy strategy = new ConfigureResponseStrategy(control, fieldTypeLibrary, siteAddress, eventPublisher, cache);
        
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
     }
}
