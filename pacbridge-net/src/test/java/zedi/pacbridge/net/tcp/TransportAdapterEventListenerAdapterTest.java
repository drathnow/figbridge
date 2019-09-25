package zedi.pacbridge.net.tcp;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.TransportAdapter;
import zedi.pacbridge.net.annotations.TransportAdapterClosed;
import zedi.pacbridge.net.annotations.TransportAdapterConnectFailed;
import zedi.pacbridge.net.annotations.TransportAdapterConnected;
import zedi.pacbridge.net.annotations.TransportAdapterConnecting;
import zedi.pacbridge.net.annotations.TransportAdapterReceivedData;
import zedi.pacbridge.net.annotations.TransportAdapterWritingData;
import zedi.pacbridge.test.BaseTestCase;

public class TransportAdapterEventListenerAdapterTest extends BaseTestCase {
    @Mock
    private TransportAdapter transportAdapter;
    @Mock
    private ByteBuffer byteBuffer;
    
    @Test
    public void shouldParseListenerClass() throws Exception {
        MyEventListener object = new MyEventListener();
        TransportAdapterEventListenerAdapter eventListenerAdapter = new TransportAdapterEventListenerAdapter(object, transportAdapter);
        
        eventListenerAdapter.postCloseEvent();
        assertTrue(object.closedCalled);
        assertSame(transportAdapter, object.closedAdapter);
        
        eventListenerAdapter.postConnectedEvent();
        assertTrue(object.connectedCalled);
        assertSame(transportAdapter, object.connectedAdapter);
        
        eventListenerAdapter.postConnectingEvent();
        assertTrue(object.connectingCalled);
        assertSame(transportAdapter, object.connectingAdapter);
        
        eventListenerAdapter.postConnectFailedEvent();
        assertTrue(object.connectFailedCalled);
        assertSame(transportAdapter, object.connectFailedAdapter);
        
        eventListenerAdapter.postReceivedDataEvent(byteBuffer);
        assertTrue(object.receiviedDataCalled);
        assertSame(transportAdapter, object.receivedAdapter);
        assertSame(byteBuffer, object.receivedBuffer);

        eventListenerAdapter.postWritingDataEvent(byteBuffer);
        assertTrue(object.writeDataCalled);
        assertSame(transportAdapter, object.writeAdapter);
        assertSame(byteBuffer, object.writeBuffer);
    }
    
    private static class MyEventListener {

        boolean closedCalled;
        TransportAdapter closedAdapter;
        
        boolean connectedCalled;
        TransportAdapter connectedAdapter;
        
        boolean connectingCalled;
        TransportAdapter connectingAdapter;

        boolean connectFailedCalled;
        TransportAdapter connectFailedAdapter;

        boolean receiviedDataCalled;
        TransportAdapter receivedAdapter;
        ByteBuffer receivedBuffer;
        
        boolean writeDataCalled;
        TransportAdapter writeAdapter;
        ByteBuffer writeBuffer;
        
        @TransportAdapterClosed
        public void closedListener(TransportAdapter transportAdapter) {
            this.closedCalled = true;
            this.closedAdapter = transportAdapter;
        }
        
        @TransportAdapterConnected
        private void connectedListener(TransportAdapter transportAdapter) {
            this.connectedCalled = true;
            this.connectedAdapter = transportAdapter;
        }
        
        @TransportAdapterConnecting
        private void connectingListener(TransportAdapter transportAdapter) {
            this.connectingCalled = true;
            this.connectingAdapter = transportAdapter;
        }

        @TransportAdapterConnectFailed
        private void connectFailedListener(TransportAdapter transportAdapter) {
            this.connectFailedCalled = true;
            this.connectFailedAdapter = transportAdapter;
        }

        @TransportAdapterReceivedData
        public void receivedData(TransportAdapter transportAdapter, ByteBuffer byteBuffer) {
            this.receiviedDataCalled = true;
            this.receivedAdapter = transportAdapter;
            this.receivedBuffer = byteBuffer;
        }

        @TransportAdapterWritingData
        private void writeData(TransportAdapter transportAdapter, ByteBuffer byteBuffer) {
            this.writeDataCalled = true;
            this.writeAdapter = transportAdapter;
            this.writeBuffer = byteBuffer;
        }
    }
}