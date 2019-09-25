package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.gdn.messages.GdnMessageType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.SiteAddress;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ControlRequestProcessor.class)
public class ControlRequestProcessorTest extends BaseTestCase {
    private static final String ERROR_MSG = "Error";
    private static final String ADDRESS_STRING = "1.2.3.4/12";
    private static final String REQUEST_ID = "123-456";
    
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private ControlRequest controlRequest;
    @Mock
    private Control control1;
    @Mock
    private ControlRequestProgressListener progressListener;
    @Mock
    private NotificationCenter notificationCenter;
    @Mock
    private ControlResponseStrategyFactory responseStrategyFactory;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(siteAddress.toString()).willReturn(ADDRESS_STRING);
        given(controlRequest.getSiteAddress()).willReturn(siteAddress);
        given(controlRequest.getControl()).willReturn(control1);
        given(control1.messageType()).willReturn(GdnMessageType.WriteIoPoint);
    }

    @Test
    public void shouldUpdateWhenStarting() throws Exception {
        ControlRequestProcessor processor = new ControlRequestProcessor(controlRequest, null, progressListener, notificationCenter);
        processor.starting();
        
        verify(controlRequest).incrementSendAttempts();
        verify(progressListener).requestProcessingStarted(controlRequest);
    }
    
    @Test
    public void shouldSignalSuccessIfAllControlsProcessedSuccessfully() throws Exception {
        Message message1 = mock(Message.class);
        
        ControlResponseStrategy strategy1 = mock(ControlResponseStrategy.class);
        
        given(responseStrategyFactory.responseStrategyForControl(control1, siteAddress)).willReturn(strategy1);
        given(controlRequest.getRequestId()).willReturn(REQUEST_ID);
        given(responseStrategyFactory.responseStrategyForControl(control1, siteAddress)).willReturn(strategy1);
        given(strategy1.isFinished()).willReturn(true);
        given(strategy1.wasSuccessful()).willReturn(true);
        
        ArgumentCaptor<ProcessedControlAttachement> arg = ArgumentCaptor.forClass(ProcessedControlAttachement.class);
        
        ControlRequestProcessor processor = new ControlRequestProcessor(controlRequest, responseStrategyFactory, progressListener, notificationCenter);
        processor.starting();
        
        assertTrue(processor.hasMoreMessages());
        assertSame(control1, processor.nextMessageWithSequenceNumber(null));
        assertTrue(processor.isExpected(message1));
        verify(strategy1).handleMessage(message1);
        
        assertFalse(processor.hasMoreMessages());
        processor.doFinalProcessing();
        verify(progressListener).requestProcessingCompleted(controlRequest, strategy1); 
        
        verify(notificationCenter).postNotificationAsync(eq(ControlRequestProcessor.CONTROL_PROCESSED_NOTIFICATION), arg.capture());
        assertSame(siteAddress, arg.getValue().getSiteAddress());
        assertSame(control1, arg.getValue().getControl());
    }
    
    @Test
    public void shouldForceFinishCurrentResponseStrategy() throws Exception {
        ControlResponseStrategy strategy = mock(ControlResponseStrategy.class);
        
        given(responseStrategyFactory.responseStrategyForControl(control1, siteAddress)).willReturn(strategy);
        given(strategy.isFinished()).willReturn(true);

        ControlRequestProcessor processor = new ControlRequestProcessor(controlRequest, responseStrategyFactory, progressListener, notificationCenter);
        processor.forceFinished(null, ""); // just to make sure!
        processor.starting();
        
        assertTrue(processor.hasMoreMessages());
        assertSame(control1, processor.nextMessageWithSequenceNumber(null));
        
        processor.forceFinished(ControlStatus.FAILURE, ERROR_MSG);
        verify(strategy).forceFinished(ControlStatus.FAILURE, ERROR_MSG);
        verify(notificationCenter, never()).postNotificationAsync(eq(ControlRequestProcessor.CONTROL_PROCESSED_NOTIFICATION), any(ProcessedControlAttachement.class));
    }
    
    @Test
    public void shouldPassResponseMessageToResponseStrategy() throws Exception {
        Message message1 = mock(Message.class);
        ControlResponseStrategy strategy1 = mock(ControlResponseStrategy.class);
        
        given(responseStrategyFactory.responseStrategyForControl(control1, siteAddress)).willReturn(strategy1);
        given(strategy1.isFinished()).willReturn(true);

        ControlRequestProcessor processor = new ControlRequestProcessor(controlRequest, responseStrategyFactory, progressListener, notificationCenter);
        processor.starting();
        
        assertTrue(processor.hasMoreMessages());
        assertSame(control1, processor.nextMessageWithSequenceNumber(null));
        assertTrue(processor.isExpected(message1));
        verify(strategy1).handleMessage(message1);
        
        assertFalse(processor.hasMoreMessages());
    }
}
