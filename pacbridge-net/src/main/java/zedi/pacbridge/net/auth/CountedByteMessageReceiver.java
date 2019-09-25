package zedi.pacbridge.net.auth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.DispatcherKey;
import zedi.pacbridge.net.core.SocketChannelWrapper;
import zedi.pacbridge.utl.HexStringEncoder;

class CountedByteMessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(CountedByteMessageReceiver.class.getName());
    public static final Integer RECEIVE_BUFFER_SIZE = 256;
    public static final Integer ZERO_READ_THRESHOLD = 5;

    private ByteBuffer byteBuffer;
    private DispatcherKey dispatcherKey;
    private SocketChannelWrapper socketChannel;
    private ReceiveState currentState;
    private MessageReceiver messageReceiver;

    CountedByteMessageReceiver(DispatcherKey dispatcherKey, SocketChannelWrapper socketChannel, MessageReceiver messageReceiver, ByteBuffer byteBuffer) {
        this.dispatcherKey = dispatcherKey;
        this.socketChannel = socketChannel;
        this.byteBuffer = byteBuffer;
        this.messageReceiver = messageReceiver;
        this.currentState = new InitialReceiveState();
    }

    public CountedByteMessageReceiver(DispatcherKey dispatcherKey, SocketChannelWrapper socketChannel, MessageReceiver messageReceiver) {
        this(dispatcherKey, socketChannel, messageReceiver, ByteBuffer.allocate(RECEIVE_BUFFER_SIZE));
    }

    void handleRead() throws IOException {
        currentState.handleRead();
    }
    
    public interface MessageReceiver {
        public boolean shouldIssueReadAferReceivingMessage(ByteBuffer byteBuffer);
        public void socketClosed();
    }

    private interface ReceiveState {
        public void handleRead() throws IOException;
    }

    private abstract class BaseReceiveState {
        protected void processRequestBytes() throws IOException {
            byteBuffer.flip();
            logger.trace("Rcv(" + byteBuffer.limit() + "): " + HexStringEncoder.bytesAsHexString(byteBuffer));
            if (messageReceiver.shouldIssueReadAferReceivingMessage(byteBuffer))
                dispatcherKey.addChannelInterest(socketChannel, SelectionKey.OP_READ);
                
        }
    }

    private class InitialReceiveState extends BaseReceiveState implements ReceiveState {
        @Override
        public void handleRead() throws IOException {
            int bytesRead = socketChannel.read(byteBuffer);
            if (bytesRead == -1)
                messageReceiver.socketClosed();
            else if (bytesRead > 0) {
                int expectedBytes = byteBuffer.get(0);
                if (expectedBytes > bytesRead) {
                    currentState = new ReadingRestOfPacketState(expectedBytes);
                    dispatcherKey.addChannelInterest(socketChannel, SelectionKey.OP_READ);
                } else
                    processRequestBytes();
            } else if (bytesRead == 0)
                dispatcherKey.addChannelInterest(socketChannel, SelectionKey.OP_READ);
        }
    }
    
    private class ReadingRestOfPacketState extends BaseReceiveState implements ReceiveState {

        private int expectedBytes;
        
        ReadingRestOfPacketState(int expectedBytes) {
            this.expectedBytes = expectedBytes;
        }
        
        @Override
        public void handleRead() throws IOException {
            int bytesRead = socketChannel.read(byteBuffer);
            if (bytesRead == -1)
                messageReceiver.socketClosed();
            else if (bytesRead > 0) {
                if (expectedBytes <= byteBuffer.remaining())
                    dispatcherKey.addChannelInterest(socketChannel, SelectionKey.OP_READ);
                else {
                    processRequestBytes();
                    currentState = new InitialReceiveState();
                }
            }
        }
    }
    
}
