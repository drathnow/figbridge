package zedi.pacbridge.stp.fad;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.test.ByteBufferMatcher;
import zedi.pacbridge.utl.crc.CrcException;

public class MessageReceiverTest extends BaseTestCase {


    private final static int SEGMENT0_ID = 0;
    private final static int MESSAGE_ID = 2;
    private static final byte[] TEST_MSG = "Hello World".getBytes();
    
    @Mock
    private MessageDeserializer messageDeserializer;
    
    @Mock
    private PendingMessageTracker messageTracker;
    
    @Mock
    private InTransitMessageTracker inTransitMessageTracker;
    
    @Mock
    private FadMessageFactory messageFactory;
    @Mock
    private FadDataHandler dataHandler;
    @Mock
    private FadMessageHandler messageSender;
    
    @Test
    public void shouldSendResendRequestOnCrcException() throws Exception {
        FadDataHandler dataHandler = mock(FadDataHandler.class);
        FadMessageHandler messageSender = mock(FadMessageHandler.class);
        Segment segment = mock(Segment.class);
        ResendMessageRequest resendRequest = mock(ResendMessageRequest.class);
        
        doThrow(new CrcException()).when(messageTracker).payloadForSegmentMessageIfComplete(segment);
        when(messageFactory.newResendMessageRequest(MESSAGE_ID, SEGMENT0_ID)).thenReturn(resendRequest);
        when(segment.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment.getSegmentId()).thenReturn(SEGMENT0_ID);
        MessageReceiver messageReceiver = new MessageReceiver(messageTracker, inTransitMessageTracker, messageFactory, dataHandler, messageSender);

        messageReceiver.handleSegmentMessage(segment);
        
        verify(messageFactory).newResendMessageRequest(MESSAGE_ID, SEGMENT0_ID);
        verify(messageSender).handleMessage(resendRequest);
    }
    
    @Test
    public void shouldHandleResendRequestForSegment() throws Exception {
        ResendRequest message = mock(ResendRequest.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        
        when(messageDeserializer.fadMessageFromByteBuffer(byteBuffer))
            .thenReturn(message);
        
        when(message.isControlMessage()).thenReturn(true);
        when(message.isResendRequest()).thenReturn(true);
        
        MessageReceiver messageReceiver = new MessageReceiver(messageTracker,inTransitMessageTracker, messageFactory, dataHandler, messageSender);
        
        messageReceiver.handleControlMessage(message);
        
        verify(inTransitMessageTracker).handleControlMessage(eq(message), eq(messageSender));
    }
    
    @Test
    public void shouldHandleAckMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        AckMessage ackMessage = mock(AckMessage.class);
        
        when(messageDeserializer.fadMessageFromByteBuffer(byteBuffer))
            .thenReturn(ackMessage);
        when(ackMessage.isControlMessage()).thenReturn(true);
        when(ackMessage.isAcknowledgement()).thenReturn(true);
        
        MessageReceiver messageReceiver = new MessageReceiver(messageTracker, inTransitMessageTracker, messageFactory, dataHandler, messageSender);

        messageReceiver.handleControlMessage(ackMessage);
        
        verify(inTransitMessageTracker).handleControlMessage(ackMessage, messageSender);
    }
    
    @Test
    public void shouldReceiveAndTrackMultiMessageSegment() throws Exception {
        Segment segment1 = mock(Segment.class);

        when(messageDeserializer.fadMessageFromByteBuffer(any(ByteBuffer.class)))
            .thenReturn(segment1);
        
        MessageReceiver messageReceiver = new MessageReceiver(messageTracker, inTransitMessageTracker, messageFactory, dataHandler, messageSender);

        messageReceiver.handleSegmentMessage(segment1);
        
        verify(messageTracker).payloadForSegmentMessageIfComplete(segment1);
        verify(dataHandler, never()).handleData(any(ByteBuffer.class));
    }

    
    @Test
    public void shouldReceiveSingleMessageSegmentAndSendAcknowledge() throws Exception {
        AckMessage ackMessage = mock(AckMessage.class);
        Segment segment1 = mock(Segment.class);

        when(messageDeserializer.fadMessageFromByteBuffer(any(ByteBuffer.class)))
            .thenReturn(segment1);
        when(messageTracker.payloadForSegmentMessageIfComplete(segment1))
            .thenReturn(TEST_MSG);
        when(messageFactory.newAckMessage(MESSAGE_ID, SEGMENT0_ID))
            .thenReturn(ackMessage);
        when(segment1.getMessageId()).thenReturn(MESSAGE_ID);
        when(segment1.getSegmentId()).thenReturn(SEGMENT0_ID);

        MessageReceiver messageReceiver = new MessageReceiver(messageTracker, inTransitMessageTracker, messageFactory, dataHandler, messageSender);

        messageReceiver.handleSegmentMessage(segment1);
        
        verify(messageTracker).payloadForSegmentMessageIfComplete(segment1);
        verify(dataHandler).handleData(argThat(matchesByteBufferContaintingBytes(TEST_MSG)));
        verify(messageSender).handleMessage(ackMessage);
    }
    
    private ByteBufferMatcher matchesByteBufferContaintingBytes(byte[] testMsg) {
        return new ByteBufferMatcher(testMsg);
    }

}
