package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.net.DefaultProtocolTap;
import zedi.pacbridge.net.LayerTap;
import zedi.pacbridge.net.LowerLayer;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.UpperLayer;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DefaultInactivityStrategy;


public class FadSendingIntegrationTest extends BaseTestCase {

    private static final byte[] LONG_BYTES = "The quick brown fox jump over the big moon".getBytes();
    private static final byte[] BYTES = "Hello World".getBytes();

    private MessageDeserializer messageDeserializer = new MessageDeserializer();
    private byte[] buffer = new byte[1024];
    private ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        GlobalScheduledExecutor.sharedInstance().clear();
    }

    @Test
    @Ignore
    public void shouldResendEntireMessageWhenRequested() throws Exception {
        System.setProperty(Fad.TRANSMIT_TIMEOUT_PROPERTY_NAME, "" + Fad.MIN_TRANSMIT_TIMEOUT);
        System.setProperty(Fad.MAX_PACKET_SIZE_PROPERTY_NAME, "" + (LONG_BYTES.length / 3));
        Fad fad = new Fad();
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));
        fad.transmitData(ByteBuffer.wrap(LONG_BYTES));

        assertEquals(3, testingLayer.sentBuffers.size());
        Segment segment1 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        Segment segment2 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        Segment segment3 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));

        ResendMessageRequest request = new ResendMessageRequest(segment3.getMessageId());

        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        request.serialize(byteBuffer);
        byteBuffer.flip();
        fad.handleReceivedData(byteBuffer);
        assertEquals(3, testingLayer.sentBuffers.size());

        segment1 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        segment2 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        segment3 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));

        ackSegmentToFad(segment3, fad);
        Thread.sleep(2500);
        assertEquals(0, testingLayer.sentBuffers.size());

        PendingMessageTracker tracker = new PendingMessageTracker(new DefaultInactivityStrategy(Fad.receiveTimeoutProperty.currentValue()));
        tracker.payloadForSegmentMessageIfComplete(segment1);
        tracker.payloadForSegmentMessageIfComplete(segment2);
        byte[] payload = tracker.payloadForSegmentMessageIfComplete(segment3);
        assertEquals(new String(LONG_BYTES), new String(payload));
    }

    @Test
    @Ignore
    public void shouldResendSingleSegmentWhenRequested() throws Exception {
        System.setProperty(Fad.TRANSMIT_TIMEOUT_PROPERTY_NAME, "" + Fad.MIN_TRANSMIT_TIMEOUT);
        System.setProperty(Fad.MAX_PACKET_SIZE_PROPERTY_NAME, "" + (LONG_BYTES.length / 3));
        Fad fad = new Fad();
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));
        fad.transmitData(ByteBuffer.wrap(LONG_BYTES));

        assertEquals(3, testingLayer.sentBuffers.size());
        testingLayer.sentBuffers.remove(0);
        Segment segment2 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        Segment segment3 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));

        ResendSegmentRequest request = new ResendSegmentRequest(segment2.getMessageId(), segment2.getSegmentId());

        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        request.serialize(byteBuffer);
        byteBuffer.flip();
        fad.handleReceivedData(byteBuffer);
        assertEquals(1, testingLayer.sentBuffers.size());

        Segment resentSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));

        assertEquals(segment2.getMessageId(), resentSegment.getMessageId());
        assertEquals(segment2.getSegmentId(), resentSegment.getSegmentId());

        ackSegmentToFad(segment3, fad);
        Thread.sleep(2500);
        assertEquals(0, testingLayer.sentBuffers.size());
    }

    @Test
    @Ignore
    public void shouldRespectWindowSize() throws Exception {
        int windowSize = FadHeader.MAX_MESSAGE_ID + 1;
        Fad fad = new Fad();
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));

        for (int i = 0; i < windowSize + 2; i++)
            fad.transmitData(ByteBuffer.wrap(BYTES));

        assertEquals(windowSize, testingLayer.sentBuffers.size());

        assertEquals(windowSize, fad.getInTransitMessagesCount());
        assertEquals(2, fad.getQueuedMessageCount());

        Segment fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize, fad.getInTransitMessagesCount());
        assertEquals(windowSize, testingLayer.sentBuffers.size());
        assertEquals(1, fad.getQueuedMessageCount());

        fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize, fad.getInTransitMessagesCount());
        assertEquals(windowSize, testingLayer.sentBuffers.size());
        assertEquals(0, fad.getQueuedMessageCount());

        fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize - 1, fad.getInTransitMessagesCount());
        assertEquals(windowSize - 1, testingLayer.sentBuffers.size());
        assertEquals(0, fad.getQueuedMessageCount());

        fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize - 2, fad.getInTransitMessagesCount());
        assertEquals(windowSize - 2, testingLayer.sentBuffers.size());
        assertEquals(0, fad.getQueuedMessageCount());

        fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize - 3, fad.getInTransitMessagesCount());
        assertEquals(windowSize - 3, testingLayer.sentBuffers.size());
        assertEquals(0, fad.getQueuedMessageCount());

        fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.remove(0));
        ackSegmentToFad(fadSegment, fad);
        assertEquals(windowSize - 4, fad.getInTransitMessagesCount());
        assertEquals(windowSize - 4, testingLayer.sentBuffers.size());
        assertEquals(0, fad.getQueuedMessageCount());
    }

    @Test
    @Ignore
    public void shouldSendMultipleSegmentsForLongMessage() throws Exception {
        System.setProperty(Fad.MAX_PACKET_SIZE_PROPERTY_NAME, "" + (LONG_BYTES.length / 2));
        Fad fad = new Fad();
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));

        fad.transmitData(ByteBuffer.wrap(LONG_BYTES));

        assertEquals(2, testingLayer.sentBuffers.size());

        Segment fadSegment1 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.get(0));
        Segment fadSegment2 = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.get(1));
        AckMessage ackMessage = new AckMessage(fadSegment2.getMessageId(), fadSegment2.getSegmentId());
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();

        fad.handleReceivedData(byteBuffer);

        PendingMessageTracker pendingMessageTracker = new PendingMessageTracker(new DefaultInactivityStrategy(Fad.receiveTimeoutProperty.currentValue()));
        byte[] payload = pendingMessageTracker.payloadForSegmentMessageIfComplete(fadSegment1);
        assertNull(payload);
        payload = pendingMessageTracker.payloadForSegmentMessageIfComplete(fadSegment2);
        assertEquals(new String(LONG_BYTES), new String(payload));
    }

    @Test
    @Ignore
    public void shouldResendIndividualMessageWhenAckNotRecieved() throws Exception {
        final Object syncObject = new Object();
        System.setProperty(Fad.TRANSMIT_TIMEOUT_PROPERTY_NAME, "" + Fad.MIN_TRANSMIT_TIMEOUT);
        Fad fad = new Fad();
//        fad.setAstRequester(new ThreadContext() {
//            @Override
//            public void requestTrap(ThreadContextHandler timeSliceHandler) {
//                synchronized (syncObject) {
//                    syncObject.notify();
//                }
//            }
//            @Override
//            public Timer getTimer() {
//                return null;
//            }
//        });
        
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));

//        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getQueueLength());
        
        synchronized (syncObject) {
            fad.transmitData(ByteBuffer.wrap(BYTES));

            assertEquals(1, testingLayer.sentBuffers.size());
            assertTrue(fad.isActive());
//            assertEquals(1, GlobalScheduledExecutor.sharedInstance().getQueueLength());
            testingLayer.sentBuffers.clear();

            syncObject.wait(2500);
        }
        
        fad.handleSyncTrap();

        assertEquals(1, testingLayer.sentBuffers.size());
        assertTrue(fad.isActive());
//        assertEquals(1, GlobalScheduledExecutor.sharedInstance().getQueueLength());

        Segment fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.get(0));

        AckMessage ackMessage = new AckMessage(fadSegment.getMessageId(), fadSegment.getSegmentId());
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();

        fad.handleReceivedData(byteBuffer);

        assertEquals(1, testingLayer.sentBuffers.size());
        assertFalse(fad.isActive());

        Thread.sleep(2500);

        assertEquals(1, testingLayer.sentBuffers.size());
        assertFalse(fad.isActive());
//        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getQueueLength());
    }

    @Test
    @Ignore
    public void shouldSendIndividualMessageAndHandleAck() throws Exception {
        System.setProperty(Fad.TRANSMIT_TIMEOUT_PROPERTY_NAME, "" + Fad.MIN_TRANSMIT_TIMEOUT);
        Fad fad = new Fad();
        TestingLayer testingLayer = new TestingLayer(fad);
        testingLayer.addLayerTap(new DefaultProtocolTap("FAD"));

//        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getQueueLength());
        fad.transmitData(ByteBuffer.wrap(BYTES));

        assertEquals(1, testingLayer.sentBuffers.size());
        assertTrue(fad.isActive());
//        assertEquals(1, GlobalScheduledExecutor.sharedInstance().getQueueLength());

        Segment fadSegment = (Segment)messageDeserializer.fadMessageFromByteBuffer(testingLayer.sentBuffers.get(0));

        AckMessage ackMessage = new AckMessage(fadSegment.getMessageId(), fadSegment.getSegmentId());
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();

        fad.handleReceivedData(byteBuffer);

        assertEquals(1, testingLayer.sentBuffers.size());
        assertFalse(fad.isActive());

        Thread.sleep(4000);

        assertEquals(1, testingLayer.sentBuffers.size());
        assertFalse(fad.isActive());
//        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getQueueLength());

        PendingMessageTracker pendingMessageTracker = new PendingMessageTracker(new DefaultInactivityStrategy(Fad.receiveTimeoutProperty.currentValue()));
        byte[] payload = pendingMessageTracker.payloadForSegmentMessageIfComplete(fadSegment);
        assertEquals(new String(BYTES), new String(payload));
    }

    private void ackSegmentToFad(Segment fadSegment, Fad fad) throws ProtocolException {
        byteBuffer.clear();
        AckMessage ackMessage = new AckMessage(fadSegment.getMessageId(), fadSegment.getSegmentId());
        ackMessage.serialize(byteBuffer);
        byteBuffer.flip();
        fad.handleReceivedData(byteBuffer);
    }

    private class TestingLayer implements LowerLayer, UpperLayer {

        private LayerTap protocolTap;
        public List<ByteBuffer> receivedBuffers = new ArrayList<ByteBuffer>();
        public List<ByteBuffer> sentBuffers = new ArrayList<ByteBuffer>();

        public TestingLayer(Fad protcolLayerUnderTest) {
//            protcolLayerUnderTest.setLowerLayer(this);
        }

        @Override
        public void setUpperLayer(UpperLayer upperLayer) {
        }

        @Override
        public void setLowerLayer(LowerLayer lowerLayer) {
        }

        @Override
        public void transmitData(ByteBuffer byteBuffer) throws IOException {
            if (protocolTap != null)
                protocolTap.bytesSent(byteBuffer);
            sentBuffers.add(copyOf(byteBuffer));
        }

        @Override
        public void handleReceivedData(ByteBuffer byteBuffer) throws ProtocolException {
            if (protocolTap != null)
                protocolTap.bytesReceived(byteBuffer);
            receivedBuffers.add(copyOf(byteBuffer));
        }

        @Override
        public void close() {
        }

        public void addLayerTap(LayerTap protocolTap) {
            this.protocolTap = protocolTap;
        }

        private ByteBuffer copyOf(ByteBuffer byteBuffer) {
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        }
    }
}
