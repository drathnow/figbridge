package zedi.pacbridge.stp.fad;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.crc.CrcCalculator;

public class MessageSenderTest extends BaseTestCase {

    private static final int MAX_PACKET_SIZE = 1024;
    private static final int CRC = 123;
    private static final int MESSAGE_ID = 23;
    
    @Mock
    private InTransitMessageTracker messageTracker;
    @Mock
    private FadMessageFactory inTransitMessageFactory;
    @Mock
    private FadMessageHandler messageHandler;
    
    
    @Test
    public void shouldClose() throws Exception {
        CrcCalculator crcCalculator = mock(CrcCalculator.class);
        MessageSender messageSender= new MessageSender(messageTracker, inTransitMessageFactory, crcCalculator, messageHandler);
        
        messageSender.close();
        
        verify(messageTracker).reset();
    }
    
    @Test
    public void shouldReset() throws Exception {
        CrcCalculator crcCalculator = mock(CrcCalculator.class);
        MessageSender messageSender = new MessageSender(messageTracker, inTransitMessageFactory, crcCalculator, messageHandler);
        
        messageSender.reset();
        
        verify(messageTracker).reset();
    }
    
    @Test
    public void shouldShouldPassResendMessageRequestToInTransitMessageTracker() throws Exception {
        CrcCalculator crcCalculator = mock(CrcCalculator.class);
        MessageSender messageSender = new MessageSender(messageTracker, inTransitMessageFactory, crcCalculator, messageHandler);
        messageSender.handleResendRequestForMessageWithMessageId(MESSAGE_ID);
        verify(messageTracker).handleResendRequestForMessageWithMessageId(MESSAGE_ID, messageHandler);
    }
    
    @Test
    public void shouldCreateAndSendInTransitMessage() throws Exception {
        CrcCalculator crcCalculator = mock(CrcCalculator.class);
        InTransitMessage inTransitMessage = mock(InTransitMessage.class);
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ByteBuffer slicedBuffer = mock(ByteBuffer.class);
        FadMessageHandler messageHandler = mock(FadMessageHandler.class);

        when(inTransitMessageFactory.newInTransitMessage(byteBuffer, MAX_PACKET_SIZE, CRC))
            .thenReturn(inTransitMessage);
        when(byteBuffer.slice()).thenReturn(slicedBuffer);
        when(inTransitMessage.getNumberOfSegments()).thenReturn(3);
        when(crcCalculator.calculate(Fad.CRC_SEED, slicedBuffer)).thenReturn(CRC);
        
        MessageSender messageSender= new MessageSender(messageTracker, inTransitMessageFactory, crcCalculator, messageHandler);
        
        messageSender.transmitData(byteBuffer);

        verify(inTransitMessageFactory).newInTransitMessage(eq(byteBuffer), eq(Fad.DEFAULT_MAX_PACKET_SIZE), eq(CRC));
        verify(messageTracker).sendAndTrackInTransitMessage(eq(inTransitMessage), eq(messageHandler));
    }
}
