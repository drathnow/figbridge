package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.annotations.ConnectionClosed;
import zedi.pacbridge.net.annotations.ConnectionConnectFailed;
import zedi.pacbridge.net.annotations.ConnectionConnected;
import zedi.pacbridge.net.annotations.ConnectionConnecting;
import zedi.pacbridge.test.BaseTestCase;

public class ConnectionListenerEventAdapterTest extends BaseTestCase {

    @Mock
    private Connection connection;
    
    @Test
    public void shouldParseListenerClass() throws Exception {
        MyEventListener object = new MyEventListener();
        ConnectionListenerEventAdapter eventListenerAdapter = new ConnectionListenerEventAdapter(connection, object);
        
        eventListenerAdapter.postClosedEvent();
        assertTrue(object.closedCalled);
        assertSame(connection, object.closedConnection);
        
        eventListenerAdapter.postConnectedEvent();
        assertTrue(object.connectedCalled);
        assertSame(connection, object.connectedconnection);
        
        eventListenerAdapter.postConnectingEvent();
        assertTrue(object.connectingCalled);
        assertSame(connection, object.connectingConnection);
        
        eventListenerAdapter.postConnectFailedEvent();
        assertTrue(object.failedCalled);
        assertSame(connection, object.failedConnection);
    }
    
    private static class MyEventListener {

        boolean closedCalled;
        Connection closedConnection;
        
        boolean connectedCalled;
        Connection connectedconnection;
        
        boolean connectingCalled;
        Connection connectingConnection;

        boolean failedCalled;
        Connection failedConnection;

        @ConnectionClosed
        public void closedListener(Connection connection) {
            this.closedCalled = true;
            this.closedConnection = connection;
        }
        
        @ConnectionConnected
        private void connectedListener(Connection connection) {
            this.connectedCalled = true;
            this.connectedconnection = connection;
        }
        
        @ConnectionConnecting
        private void connectingListener(Connection connection) {
            this.connectingCalled = true;
            this.connectingConnection = connection;
        }

        @ConnectionConnectFailed
        private void failedListener(Connection connection) {
            this.failedCalled = true;
            this.failedConnection = connection;
        }
    }
}
