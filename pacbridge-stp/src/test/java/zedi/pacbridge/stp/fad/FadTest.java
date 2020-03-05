package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.LowerLayer;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.test.SingletonTestHelper;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.ThreadContext;


public class FadTest extends BaseTestCase {

    private static final Integer MESSAGE_ID = 42;
    @Mock
    private MessageDeserializer messageDeserializer;
    @Mock
    private GlobalScheduledExecutor globalScheduledExecutor;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SingletonTestHelper.replaceStaticInstance(GlobalScheduledExecutor.class, globalScheduledExecutor);
    }

    @Override
    public void tearDown() throws Exception {
        SingletonTestHelper.replaceStaticInstance(GlobalScheduledExecutor.class, null);
        super.tearDown();
    }

    @Test
    public void shouldRemoveNextPendingResendRequestMessageIdAndPassToMessageSender() throws Exception {
        MessageReceiver messageReceiver = mock(MessageReceiver.class);
        MessageSender messageSender = mock(MessageSender.class);
        ThreadContext timeSliceRequester = mock(ThreadContext.class);
        
        Fad fad = new Fad(messageDeserializer, messageReceiver, messageSender);
        fad.setAstRequester(timeSliceRequester);
        
        fad.getRetransmitEventHandler().retransmitMessageWithMessageId(MESSAGE_ID);
        
        verify(timeSliceRequester).requestTrap(fad);
        assertTrue(fad.getRetransmitMessageQueue().contains(MESSAGE_ID));
        
        fad.handleSyncTrap();
        
        verify(messageSender).handleResendRequestForMessageWithMessageId(MESSAGE_ID);
        assertFalse(fad.getRetransmitMessageQueue().contains(MESSAGE_ID));
    }
    
    @Test
    public void shouldQueueMessageIdAndRequestATimeSliceWhenAskedToResendMessage() throws Exception {
        MessageReceiver messageReceiver = mock(MessageReceiver.class);
        MessageSender messageSender = mock(MessageSender.class);
        ThreadContext timeSliceRequester = mock(ThreadContext.class);
        
        Fad fad = new Fad(messageDeserializer, messageReceiver, messageSender);
        fad.setAstRequester(timeSliceRequester);
        
        fad.getRetransmitEventHandler().retransmitMessageWithMessageId(MESSAGE_ID);
        
        verify(timeSliceRequester).requestTrap(fad);
        assertTrue(fad.getRetransmitMessageQueue().contains(MESSAGE_ID));
    }
    
    @Test
    public void shouldClose() throws Exception {
        MessageReceiver messageReceiver = mock(MessageReceiver.class);
        MessageSender messageSender = mock(MessageSender.class);
        LowerLayer layer = mock(LowerLayer.class);
        
        Fad fad = new Fad(messageDeserializer, messageReceiver, messageSender);
        fad.setLowerLayer(layer);
        fad.close();
        
        verify(messageReceiver).close();
        verify(messageSender).close();
        verify(messageSender).reset();
        verify(messageReceiver).reset();
    }
    
    @Test
    public void shouldHandleControlMessage() throws Exception {
        MessageReceiver messageReceiver = mock(MessageReceiver.class);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ControlMessage controlMessage = mock(ControlMessage.class);
        
        when(messageDeserializer.fadMessageFromByteBuffer(byteBuffer)).thenReturn(controlMessage);
        when(controlMessage.isControlMessage()).thenReturn(true);
        
        Fad fad = new Fad(messageDeserializer, messageReceiver, null);
        
        fad.handleReceivedData(byteBuffer);
        
        verify(messageReceiver).handleControlMessage(controlMessage);
    }
    
    @Test
    public void shouldHandleSegmentMessage() throws Exception {
        MessageReceiver messageReceiver = mock(MessageReceiver.class);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        Segment segment = mock(Segment.class);
        
        when(messageDeserializer.fadMessageFromByteBuffer(byteBuffer)).thenReturn(segment);
        
        Fad fad = new Fad(messageDeserializer, messageReceiver, null);
        
        fad.handleReceivedData(byteBuffer);
        
        verify(messageReceiver).handleSegmentMessage(segment);
    }
}
