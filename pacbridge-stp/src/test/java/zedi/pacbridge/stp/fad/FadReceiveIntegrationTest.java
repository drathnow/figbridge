package zedi.pacbridge.stp.fad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.net.LayerTap;
import zedi.pacbridge.net.LowerLayer;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.UpperLayer;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.Crc16Reflect;
import zedi.pacbridge.utl.crc.CrcCalculator;

public class FadReceiveIntegrationTest extends BaseTestCase {

    private static final byte[] LONG_BYTES = "The quick brown fox jump over the big moon".getBytes();
    private static final byte[] BYTES = "Hello World".getBytes();
    private static final int MAX_PACKET_SIZE = 1024;
    
    private SerializationLayer serializationLayer = new SerializationLayer();
    private ByteBuffer sendingByteBuffer = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalScheduledExecutor.sharedInstance().clear();
    }
    
    @Test
    public void shouldDecodeMultipleSegmentMessageFromOldStp() throws Exception {
        byte[][] oldStpSegments = new byte[][]
                {
                    HexStringDecoder.hexStringAsBytes("40 00 0C 26 00 7B 03 4F 51"),
                    HexStringDecoder.hexStringAsBytes("41 00 19 5B 01 00 00 01 08 00"), 
                    HexStringDecoder.hexStringAsBytes("C2 09 78 D1 40 13 33 33"),
                };
        
        MyLayer layer = new MyLayer();
        Fad fad = new Fad();
        fad.setUpperLayer(layer);
        for (byte[] bytes : oldStpSegments)
            fad.handleReceivedData(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
        assertNotNull(layer.bytes);
    }
    
    @Test
    public void shouldDecodeMessageFromOldStp() throws Exception {
        MyLayer layer = new MyLayer();
        String oldStpByteString = "C0 09 B1 B4 0C 26 00 7B 03 4F 4F E9 D9 01 00 00 01 08 00 40 13 33 33";
        byte[] bytes = HexStringDecoder.hexStringAsBytes(oldStpByteString);
        Fad fad = new Fad();
        fad.setUpperLayer(layer);
        fad.handleReceivedData(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
        assertNotNull(layer.bytes);
    }
    
    
    @Test
    public void shouldDiscardMessageIfCrcIsBad() throws Exception {
        System.setProperty(Fad.RECEIVE_TIMEOUT_PROPERTY_NAME, ""+ Fad.MIN_RECEIVE_TIMEOUT);
        Fad fad = new Fad();
        ByteBuffer testBuffer = ByteBuffer.wrap(LONG_BYTES);
        TestingLayer testingLayer = new TestingLayer(fad);
        InTransitMessage inTransitMessage = new InTransitMessage(testBuffer, (LONG_BYTES.length/3), 88, 1);
        inTransitMessage.transmitThroughMessageTransmitter(serializationLayer, sendingByteBuffer);
        
        for (ByteBuffer bb : serializationLayer.sentBuffers)
            fad.handleReceivedData(bb);
                
        Thread.sleep(2500);
        assertEquals(0, fad.getPendingMessagesCount());
        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getCurrentNumberOfScheduledTasks());
        assertEquals(0, testingLayer.receivedBuffers.size());
    }

    @Test
    public void shouldDisgardIncompletePendingMessageWhenPendingTimerExpires() throws Exception {
        System.setProperty(Fad.RECEIVE_TIMEOUT_PROPERTY_NAME, ""+ Fad.MIN_RECEIVE_TIMEOUT);
        Fad fad = new Fad();
        ByteBuffer testBuffer = ByteBuffer.wrap(LONG_BYTES);
        new TestingLayer(fad);
        CrcCalculator crcCalculator = new Crc16Reflect();
        int crc = crcCalculator.calculate(Fad.CRC_SEED, testBuffer.slice());
        InTransitMessage inTransitMessage = new InTransitMessage(testBuffer, (LONG_BYTES.length/3), crc, 1);
        inTransitMessage.transmitThroughMessageTransmitter(serializationLayer, sendingByteBuffer);
        
        fad.handleReceivedData(serializationLayer.sentBuffers.get(0));

        assertEquals(1, fad.getPendingMessagesCount());
        assertEquals(1, GlobalScheduledExecutor.sharedInstance().getCurrentNumberOfScheduledTasks());
        Thread.sleep(2500);
        assertEquals(0, fad.getPendingMessagesCount());
        assertEquals(0, GlobalScheduledExecutor.sharedInstance().getCurrentNumberOfScheduledTasks());
    }
    
    @Test
    public void shouldReceiveLongMessageInMultipleSegments() throws Exception {
        Fad fad = new Fad();
        ByteBuffer testBuffer = ByteBuffer.wrap(LONG_BYTES);
        TestingLayer testingLayer = new TestingLayer(fad);
        CrcCalculator crcCalculator = new Crc16Reflect();
        int crc = crcCalculator.calculate(Fad.CRC_SEED, testBuffer.slice());
        InTransitMessage inTransitMessage = new InTransitMessage(testBuffer, (LONG_BYTES.length/3), crc, 1);
        inTransitMessage.transmitThroughMessageTransmitter(serializationLayer, sendingByteBuffer);
        
        for (ByteBuffer bb : serializationLayer.sentBuffers)
            fad.handleReceivedData(bb);
                
        assertEquals(1, testingLayer.receivedBuffers.size());
        ByteBuffer bb = testingLayer.receivedBuffers.remove(0);
        assertEquals(new String(LONG_BYTES), new String(bb.array(), 0, bb.limit()));
    }
    
    @Test
    public void shouldRecieveSingleMessage() throws Exception {
        Fad fad = new Fad();
        ByteBuffer testBuffer = ByteBuffer.wrap(BYTES);
        TestingLayer testingLayer = new TestingLayer(fad);
        CrcCalculator crcCalculator = new Crc16Reflect();
        int crc = crcCalculator.calculate(Fad.CRC_SEED, testBuffer.slice());
        InTransitMessage inTransitMessage = new InTransitMessage(testBuffer, MAX_PACKET_SIZE, crc, 1);
        inTransitMessage.transmitThroughMessageTransmitter(serializationLayer, sendingByteBuffer);
        
        for (ByteBuffer bb : serializationLayer.sentBuffers)
            fad.handleReceivedData(bb);
                
        assertEquals(1, testingLayer.receivedBuffers.size());
        ByteBuffer bb = testingLayer.receivedBuffers.remove(0);
        assertEquals(new String(BYTES), new String(bb.array(), 0, bb.limit()));
    }
    
    private class SerializationLayer implements FadMessageTransmitter {
        public List<ByteBuffer> sentBuffers = new ArrayList<ByteBuffer>();
        
        @Override
        public void transmitByteBuffer(ByteBuffer byteBuffer) throws IOException {
            sentBuffers.add(copyOf(byteBuffer));
        }
        
        protected ByteBuffer copyOf(ByteBuffer byteBuffer) {
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        }        
    }
    
    private class TestingLayer implements LowerLayer, UpperLayer {

        private LayerTap protocolTap;
        public List<ByteBuffer> receivedBuffers = new ArrayList<ByteBuffer>();
        public List<ByteBuffer> sentBuffers = new ArrayList<ByteBuffer>();
        
        public TestingLayer(Fad protcolLayerUnderTest) {
            protcolLayerUnderTest.setUpperLayer(this);
            protcolLayerUnderTest.setLowerLayer(this);
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

        private ByteBuffer copyOf(ByteBuffer byteBuffer) {
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        }
    }    
    
    
    private class MyLayer extends ProtocolLayerAdapter {
        
        public byte[] bytes;
        
        @Override
        public void handleReceivedData(ByteBuffer byteBuffer) throws ProtocolException {
            bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
        }
    }

}
