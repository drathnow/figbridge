package zedi.pacbridge.app.controls.zap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.events.EventStatus;
import zedi.pacbridge.app.events.zios.ZiosEventResponseEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckDetailsType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.ProtocolErrorDetails;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;
import zedi.pacbridge.zap.messages.WriteIoPointsControlAckDetails;
import zedi.pacbridge.zap.messages.WriteValue;
import zedi.pacbridge.zap.messages.WriteValueAck;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WriteIoPointsControlResponseStrategy.class, ZiosEventResponseEvent.class})
public class WriteIoPointsControlResponseStrategyTest extends BaseTestCase {
    private static final String ADDRESS = "DavesNotHere";
    private static final Long IOID1 = 1L;
    private static final Long IOID2 = 2L;
    private static final Long IOID3 = 3L;
    private static final Integer SEQ_NO = 42;
    private static final Long COMMAND_ID = 110L;
    private static final String ERROR_MSG = "Foo";
    
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
    public void shouldForceFinishIfProtocolErrorHappens() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        ProtocolErrorDetails errorDetails = mock(ProtocolErrorDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NO);
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.isProtocolError()).willReturn(true);
        given(ackMessage.additionalDetails()).willReturn(errorDetails);
        given(control.sequenceNumber()).willReturn(SEQ_NO);
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(control.getEventId()).willReturn(COMMAND_ID);
        given(errorDetails.toString()).willReturn(ERROR_MSG);

        whenNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(ERROR_MSG), eq(null)).thenReturn(event);

        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        strategy.completeProcessing();
        
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        verifyNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(ERROR_MSG), eq(null));
        verify(eventPublisher).publishEvent(event);
    }
    
    @Test
    public void shouldForceFinish() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
     
        whenNew(ZiosEventResponseEvent.class)
        	.withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(ERROR_MSG), eq(null))
        	.thenReturn(event);
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(control.getEventId()).willReturn(COMMAND_ID);
        
        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.forceFinished(ControlStatus.FAILURE, ERROR_MSG);
        assertTrue(strategy.isFinished());
        
        strategy.completeProcessing();
        
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        verifyNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(ERROR_MSG), eq(null));
        verify(eventPublisher).publishEvent(event);
        
    }

    @Test
    public void shouldDoNothingIfIncorrectSequenceNumber() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteValue value1 = mock(WriteValue.class);
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(value1);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        given(control.getWriteValues()).willReturn(writeValues);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NO);

        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, null, cache);
        strategy.handleMessage(ackMessage);
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        assertFalse(strategy.isFinished());
    }
    
    @Test
    public void shouldDoNothingIfIncorrectAckMessageType() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteValue value1 = mock(WriteValue.class);
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(value1);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        given(control.getWriteValues()).willReturn(writeValues);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.BundledReport);

        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, null, cache);
        strategy.handleMessage(ackMessage);

        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        assertFalse(strategy.isFinished());
    }
    
    @Test
    public void shouldHandleAckMessageWithNoWriteResponses() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        Map<Long, WriteValueAck> ackMap = new HashMap<>();
        WriteIoPointsControlAckDetails ackDetails = mock(WriteIoPointsControlAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteValue value1 = mock(WriteValue.class);
        
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(value1);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);

        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.isProtocolError()).willReturn(false);
        given(ackMessage.additionalDetails()).willReturn(ackDetails);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NO);
        given(ackDetails.type()).willReturn(AckDetailsType.WriteIoPoints);
        given(control.getWriteValues()).willReturn(writeValues);
        given(control.sequenceNumber()).willReturn(SEQ_NO);
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(control.getEventId()).willReturn(COMMAND_ID);
        given(value1.getIoId()).willReturn(IOID1);
        given(control.getWriteValues()).willReturn(writeValues);
        given(ackDetails.ackMap()).willReturn(ackMap);
        
        whenNew(ZiosEventResponseEvent.class)
        	.withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(WriteIoPointsControlResponseStrategy.NO_ACKS_ERROR), eq(null))
        	.thenReturn(event);

        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());
        strategy.completeProcessing();
        
        verifyNew(ZiosEventResponseEvent.class)
        	.withArguments(eq(COMMAND_ID), eq(EventStatus.Failure), eq(ADDRESS), eq(WriteIoPointsControlResponseStrategy.NO_ACKS_ERROR), eq(null));
        verify(cache, never()).markSiteAsInteresting(siteAddress.getAddress());
        verify(eventPublisher).publishEvent(event);
//        
//        assertEquals(ControlStatus.FAILURE, strategy.finalStatus());
//        assertEquals(WriteIoPointsControlResponseStrategy.NO_ACKS_ERROR, strategy.finalStatusMessage());
    }

    @Ignore // We are ignoring this becuase we currently don't allow multiple write operations in a single write IO Points control.
    @Test
    public void shouldHandleAckMessageWithWriteResponseWithAtLeastOneFailure() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        Map<Long, WriteValueAck> ackMap = new HashMap<>();
        WriteIoPointsControlAckDetails ackDetails = mock(WriteIoPointsControlAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteValueAck valueAck1 = mock(WriteValueAck.class);
        WriteValueAck valueAck2 = mock(WriteValueAck.class);
        WriteValueAck valueAck3 = mock(WriteValueAck.class);
        WriteValue value1 = mock(WriteValue.class);
        WriteValue value2 = mock(WriteValue.class);
        WriteValue value3 = mock(WriteValue.class);
        
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(value1);
        writeValues.add(value2);
        writeValues.add(value3);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        ackMap.put(IOID1, valueAck1);
        ackMap.put(IOID2, valueAck2);
        ackMap.put(IOID3, valueAck2);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.additionalDetails()).willReturn(ackDetails);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NO);
        given(ackDetails.type()).willReturn(AckDetailsType.WriteIoPoints);
        given(control.getWriteValues()).willReturn(writeValues);
        given(control.sequenceNumber()).willReturn(SEQ_NO);
        given(control.getEventId()).willReturn(COMMAND_ID);
        
        given(valueAck1.iodId()).willReturn(IOID1);
        given(valueAck1.isSuccess()).willReturn(true);
        given(valueAck2.iodId()).willReturn(IOID2);
        given(valueAck2.isSuccess()).willReturn(false);
        given(valueAck3.iodId()).willReturn(IOID3);
        given(valueAck3.isSuccess()).willReturn(false);
        
        given(value1.getIoId()).willReturn(IOID1);
        given(value2.getIoId()).willReturn(IOID2);
        given(value3.getIoId()).willReturn(IOID3);
        
        given(control.getWriteValues()).willReturn(writeValues);
        given(ackDetails.ackMap()).willReturn(ackMap);
        
        whenNew(ZiosEventResponseEvent.class).withArguments(eq(COMMAND_ID), eq(EventStatus.Failure)).thenReturn(event);
        
        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, null, cache);
        strategy.handleMessage(ackMessage);
        assertTrue(strategy.isFinished());

//        assertTrue(str.startsWith(WriteIoPointsControlResponseStrategy.ERROR_MSG));
//        JSONArray array = new JSONArray(str.substring(str.indexOf("["), str.indexOf("]")+1));
//        assertEquals(IOID2.toString(), array.get(0).toString());
//        assertEquals(IOID3.toString(), array.get(1).toString());
    }

    
    @Test
    public void shouldHandleAckMessageWithWriteResponseAllSuccess() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ZiosEventResponseEvent event = mock(ZiosEventResponseEvent.class);
        Map<Long, WriteValueAck> ackMap = new HashMap<>();
        WriteIoPointsControlAckDetails ackDetails = mock(WriteIoPointsControlAckDetails.class);
        AckMessage ackMessage = mock(AckMessage.class);
        WriteValueAck valueAck = mock(WriteValueAck.class);
        WriteValue value1 = mock(WriteValue.class);
        
        List<WriteValue> writeValues = new ArrayList<>();
        writeValues.add(value1);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        ackMap.put(IOID1, valueAck);
        
        given(ackMessage.messageType()).willReturn(ZapMessageType.Acknowledgement);
        given(ackMessage.additionalDetails()).willReturn(ackDetails);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQ_NO);
        given(ackMessage.isProtocolError()).willReturn(false);
        given(ackDetails.type()).willReturn(AckDetailsType.WriteIoPoints);
        given(control.getEventId()).willReturn(COMMAND_ID);
        given(control.getWriteValues()).willReturn(writeValues);
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(control.sequenceNumber()).willReturn(SEQ_NO);
        given(control.getWriteValues()).willReturn(writeValues);
        given(valueAck.iodId()).willReturn(IOID1);
        given(valueAck.isSuccess()).willReturn(true);
        given(value1.getIoId()).willReturn(IOID1);
        given(ackDetails.ackMap()).willReturn(ackMap);
        
        whenNew(ZiosEventResponseEvent.class).withArguments(COMMAND_ID, EventStatus.Success, ADDRESS).thenReturn(event);
        
        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, eventPublisher, cache);
        strategy.handleMessage(ackMessage);
        
        assertTrue(strategy.isFinished());
        verify(cache).markSiteAsInteresting(siteAddress.getAddress());
        verifyNew(ZiosEventResponseEvent.class).withArguments(COMMAND_ID, EventStatus.Success, ADDRESS);
        verify(eventPublisher).publishEvent(event);
    }
    
    @Test
    public void shouldReturnProperFinishCodeWhenInitialized() throws Exception {
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        WriteIoPointsControlResponseStrategy strategy = new WriteIoPointsControlResponseStrategy(control, siteAddress, null, null);
        assertFalse(strategy.isFinished());
    }
    
}
