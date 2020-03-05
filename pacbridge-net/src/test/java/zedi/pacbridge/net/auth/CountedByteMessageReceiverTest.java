package zedi.pacbridge.net.auth;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.test.BaseTestCase;

public class CountedByteMessageReceiverTest extends BaseTestCase {
    private static final byte[] CLOSE_ME = new byte[1];
    private static final byte[] COUNTED_BYTES = new byte[]{0x05, 0x01, 0x02, 0x03, 0x04, 0x05};
    private static final byte[] PARTIAL_BYTES1 = new byte[]{0x05, 0x01, 0x02};
    private static final byte[] PARTIAL_BYTES2 = new byte[]{0x03, 0x04, 0x05};

    @Test
    public void shouldSiganlCloseAfterPartialRead() throws Exception {
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        MockSocketChannel mockSocketChannel = new MockSocketChannel();
        mockSocketChannel.bytesToBeRead.add(PARTIAL_BYTES1);
        mockSocketChannel.bytesToBeRead.add(CLOSE_ME);
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        CountedByteMessageReceiver.MessageReceiver listener = mock(CountedByteMessageReceiver.MessageReceiver.class);
        
        given(listener.shouldIssueReadAferReceivingMessage(byteBuffer)).willReturn(true);
        CountedByteMessageReceiver receiver = new CountedByteMessageReceiver(dispatcherKey, mockSocketChannel, listener, byteBuffer);
        
        receiver.handleRead();
        verify(dispatcherKey).addChannelInterest(mockSocketChannel, SelectionKey.OP_READ);
        verify(dispatcherKey).addChannelInterest(mockSocketChannel, SelectionKey.OP_READ);
        verify(dispatcherKey).addChannelInterest(mockSocketChannel, SelectionKey.OP_READ);

        receiver.handleRead();
        verify(listener).socketClosed();
    }
    
    
    @Test
    public void shouldSignalCloseSocket() throws Exception {
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        SocketChannelWrapper socketChannel = mock(SocketChannelWrapper.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        CountedByteMessageReceiver.MessageReceiver listener = mock(CountedByteMessageReceiver.MessageReceiver.class);
        given(socketChannel.read(byteBuffer)).willReturn(-1);

        CountedByteMessageReceiver receiver = new CountedByteMessageReceiver(dispatcherKey, socketChannel, listener, byteBuffer);
        receiver.handleRead();
        
        verify(socketChannel).read(byteBuffer);
        verify(listener).socketClosed();
    }
    
    @Test
    public void shouldReceivePartialMessage() throws Exception {
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        MockSocketChannel mockSocketChannel = new MockSocketChannel();
        mockSocketChannel.bytesToBeRead.add(PARTIAL_BYTES1);
        mockSocketChannel.bytesToBeRead.add(PARTIAL_BYTES2);
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        
        CountedByteMessageReceiver.MessageReceiver listener = new CountedByteMessageReceiver.MessageReceiver() {
            @Override
            public void socketClosed() {
            }
            @Override
            public boolean shouldIssueReadAferReceivingMessage(ByteBuffer byteBuffer) {
                byte[] bytes = new byte[byteBuffer.limit()];
                byteBuffer.get(bytes);
                assertTrue(Arrays.equals(COUNTED_BYTES, bytes));
                return false;
            }
        };
        
        CountedByteMessageReceiver receiver = new CountedByteMessageReceiver(dispatcherKey, mockSocketChannel, listener, byteBuffer);
        
        receiver.handleRead();
    }
    
    @Test
    public void shouldReceiveMessageInSingleReads() throws Exception {
        DispatcherKey dispatcherKey = mock(DispatcherKey.class);
        MockSocketChannel mockSocketChannel = new MockSocketChannel();
        mockSocketChannel.bytesToBeRead.add(COUNTED_BYTES);
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        
        CountedByteMessageReceiver.MessageReceiver listener = new CountedByteMessageReceiver.MessageReceiver() {
            @Override
            public boolean shouldIssueReadAferReceivingMessage(ByteBuffer byteBuffer) {
                byte[] bytes = new byte[byteBuffer.limit()];
                byteBuffer.get(bytes);
                assertTrue(Arrays.equals(COUNTED_BYTES, bytes));
                return false;
            }
            @Override
            public void socketClosed() {
            }
        }; 
        
        CountedByteMessageReceiver receiver = new CountedByteMessageReceiver(dispatcherKey, mockSocketChannel, listener, byteBuffer);
        
        receiver.handleRead();
    }
    
    private class MockSocketChannel extends SocketChannelWrapper {
        List<byte[]> bytesToBeRead = new ArrayList<>();
        List<byte[]> bytesWritten= new ArrayList<>();
        int count;
        @SuppressWarnings("unused")
		boolean closeCalled;
        
        public MockSocketChannel() {
            super(null);
        }

        @Override
        public int read(ByteBuffer byteBuffer) throws IOException {
            if (count == bytesToBeRead.size())
                return 0;
            byte[] bytes = bytesToBeRead.get(count++);
            if (bytes == CLOSE_ME)
                return -1;
            byteBuffer.put(bytes);
            return bytes.length;
        }
        
        @Override
        public int write(ByteBuffer byteBuffer) throws IOException {
            int limit = byteBuffer.limit();
            byte[] bytes = new byte[limit];
            byteBuffer.get(bytes);
            bytesWritten.add(bytes);
            return limit;
        }
        
        @Override
        public void close() throws IOException {
            closeCalled = true;
        }
    }
}
