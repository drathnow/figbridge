package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.CollectionFactory;

@SuppressWarnings("unchecked")
public class InTransitMessageTest extends BaseTestCase {


    private static final byte[] TEST_MSG = "Hello World".getBytes();
    private static final int CRC = 43;
    private static final int MESSAGE_ID = 1;
    private static final int SEGMENT_ID = 2;

    @Mock
    private Map<Integer, Segment> segmentMap;
    @Mock
    private CollectionFactory collectionFactory;
    @Mock
    private FadMessageHandler messageHandler;
    
    private MessageDeserializer fadMessageDeserializer = new MessageDeserializer();
    
    @Test
    public void shouldReturnSizeOfLargestSegment() throws Exception {
        ByteBuffer immuatableBuffer = ByteBuffer.wrap(TEST_MSG).order(ByteOrder.LITTLE_ENDIAN);
        InTransitMessage inTransitMessage = new InTransitMessage(immuatableBuffer, 6, CRC, 1);
        assertEquals(FadHeader.FAD_MAX_SIZE+6, inTransitMessage.size());
    }
    
    @Test
    public void shouldShouldSetMessageIdInAllFragments() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_MSG);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        Collection<Segment> collection = mock(Collection.class);

        Segment segment1 = mock(Segment.class);
        Segment segment2 = mock(Segment.class);
        Segment[] segments = new Segment[]{segment1, segment2};
        
        when(collectionFactory.newSynchronizedMap(any(TreeMap.class))).thenReturn(segmentMap);
        when(segmentMap.values()).thenReturn(collection);
        when(collection.toArray(any(Segment[].class))).thenReturn(segments);
        when(segmentMap.get(SEGMENT_ID)).thenReturn(segment1);
        
        InTransitMessage inTransitMessage = new InTransitMessage(byteBuffer, 1024, CRC, 1, collectionFactory);

        inTransitMessage.setMessageId(MESSAGE_ID);
        
        verify(segment1).setMessageId(eq(MESSAGE_ID));
        verify(segment2).setMessageId(eq(MESSAGE_ID));
    }
    
    @Test
    public void shouldTransmitMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_MSG);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FadMessageTransmitter transmitter = mock(FadMessageTransmitter.class);

        Segment segment1 = mock(Segment.class);
        Segment segment2 = mock(Segment.class);
        ArrayList<Segment> segments = new ArrayList<Segment>();
        segments.add(segment1);
        segments.add(segment2);
        
        when(collectionFactory.newSynchronizedMap(any(TreeMap.class))).thenReturn(segmentMap);
        when(segmentMap.values()).thenReturn(segments);
        when(segmentMap.get(SEGMENT_ID)).thenReturn(segment1);
        
        InTransitMessage inTransitMessage = new InTransitMessage(byteBuffer, 1024, CRC, 1, collectionFactory);

        inTransitMessage.transmitThroughMessageTransmitter(transmitter, byteBuffer);
        
        verify(segment1).transmitThroughMessageTransmitter(transmitter, byteBuffer);
        verify(segment2).transmitThroughMessageTransmitter(transmitter, byteBuffer);
        assertEquals(1, inTransitMessage.getSendAttempts());
    }
     
    @Test
    public void shouldResendSegment() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_MSG);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        Segment segment = mock(Segment.class);
        
        when(collectionFactory.newSynchronizedMap(any(TreeMap.class))).thenReturn(segmentMap);
        when(segmentMap.get(SEGMENT_ID)).thenReturn(segment);
        
        InTransitMessage inTransitMessage = new InTransitMessage(byteBuffer, 1024, CRC, 1, collectionFactory);

        inTransitMessage.resendSegment(SEGMENT_ID, messageHandler);
        
        verify(messageHandler).handleMessage(segment);
    }

    @Test
    public void shouldHandleAckForSingleMessage() throws Exception {
        ByteBuffer immuatableBuffer = ByteBuffer.wrap(TEST_MSG).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        MockFadMessageTransmitter protocolLayer = new MockFadMessageTransmitter();

        InTransitMessage inTransitMessage = new InTransitMessage(immuatableBuffer, 0, CRC, 1);
        inTransitMessage.transmitThroughMessageTransmitter(protocolLayer, byteBuffer);
        
        assertEquals(1, protocolLayer.transmittedBuffers.size());

        byteBuffer = protocolLayer.transmittedBuffers.get(0);
        byteBuffer.flip();
        FadMessage fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        Segment segmentMessage = (Segment)fadMessage;

        AckMessage ackMessage = new AckMessage(segmentMessage.getMessageId(), segmentMessage.getSegmentId());

        inTransitMessage.setMessageStatus(FadMessageStatus.ACKNOWLEDGED);
        assertTrue(inTransitMessage.hasBeenAcknowledged());
    }
    
    @Test
    public void shouldCreateMultipleMessageForMaxPacketSizeSmallerThanMessageSize() throws Exception {
        ByteBuffer immuatableBuffer = ByteBuffer.wrap(TEST_MSG);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        MockFadMessageTransmitter protocolLayer = new MockFadMessageTransmitter();

        InTransitMessage inTransitMessage = new InTransitMessage(immuatableBuffer, 6, CRC, 1);
        inTransitMessage.transmitThroughMessageTransmitter(protocolLayer, byteBuffer);
        
        assertEquals(1, inTransitMessage.getSendAttempts());
        assertEquals(2, protocolLayer.transmittedBuffers.size());
        
        byteBuffer = protocolLayer.transmittedBuffers.get(0);
        byteBuffer.flip();
        FadMessage fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        Segment segmentMessage = (Segment)fadMessage;

        assertEquals(0, segmentMessage.getSegmentId());
        assertFalse(segmentMessage.isAcknowledgement());
        assertTrue(segmentMessage.isAcknowledgementRequired());
        assertFalse(segmentMessage.isLastSegment());
        assertFalse(segmentMessage.isResendRequest());
        assertFalse(segmentMessage.hasCrc());
        assertEquals("Hello ", new String(segmentMessage.getPayload()));

        byteBuffer = protocolLayer.transmittedBuffers.get(1);
        byteBuffer.flip();
        fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        segmentMessage = (Segment)fadMessage;

        assertEquals(1, segmentMessage.getSegmentId());
        assertFalse(segmentMessage.isAcknowledgement());
        assertTrue(segmentMessage.isAcknowledgementRequired());
        assertTrue(segmentMessage.isLastSegment());
        assertFalse(segmentMessage.isResendRequest());
        assertTrue(segmentMessage.hasCrc());
        assertEquals("World", new String(segmentMessage.getPayload()));
    }
    
    @Test
    public void shouldCreateSingleMessage() throws Exception {
        ByteBuffer immuatableBuffer = ByteBuffer.wrap(TEST_MSG).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        MockFadMessageTransmitter protocolLayer = new MockFadMessageTransmitter();

        InTransitMessage inTransitMessage = new InTransitMessage(immuatableBuffer, 0, CRC, 1);
        inTransitMessage.transmitThroughMessageTransmitter(protocolLayer, byteBuffer);
        
        assertEquals(1, protocolLayer.transmittedBuffers.size());

        byteBuffer = protocolLayer.transmittedBuffers.get(0);
        byteBuffer.flip();
        FadMessage fadMessage = fadMessageDeserializer.fadMessageFromByteBuffer(byteBuffer);
        assertTrue(fadMessage instanceof Segment);
        Segment segmentMessage = (Segment)fadMessage;

        assertEquals(0, segmentMessage.getSegmentId());
        assertFalse(segmentMessage.isAcknowledgement());
        assertTrue(segmentMessage.isAcknowledgementRequired());
        assertTrue(segmentMessage.isLastSegment());
        assertFalse(segmentMessage.isResendRequest());
        assertEquals(new String(TEST_MSG), new String(segmentMessage.getPayload()));
    }
}
