package zedi.fg.tester.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.TransmissionPackage;
import zedi.fg.tester.util.TransmissionPackage.TYPE;
import zedi.pacbridge.app.auth.zap.ZapAuthenticationStrategy;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessage;
import zedi.pacbridge.zap.messages.ConnectionFlags;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.ZapMessage;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapSessionHeader;

public class FgSession implements Runnable
{
	private static final Integer RCV_BUFFER_SIZE = 2048;
	private static final Logger logger = Logger.getLogger(FgSession.class);
	private static AtomicInteger SESSION_ID = new AtomicInteger(1);
	
	private enum State {
		WAIT_FOR_CLIENT_RESPONSE, AUTHENTICATED
	}
	
	private NotificationCenter notificationCenter;
	private byte[] rcvBuffer;
	private Integer sequence;
	private Selector selector;
	private SocketChannel socketChannel;
	private List<ByteBuffer> outputQueue;
	private ByteBuffer rcvByteBuffer;
	private int expectedBytes;
	private SelectionKey selectionKey;
	private State currentState;
	private Integer sessionId;
	private boolean shutdown;
	private FieldTypeLibrary fieldTypeLibrary;

	public FgSession(NotificationCenter notificationCenter, SocketChannel socketChannel, FieldTypeLibrary fieldTypeLibrary) throws IOException
	{
		this.notificationCenter = notificationCenter;
		this.rcvBuffer = new byte[RCV_BUFFER_SIZE];
		this.selector = Selector.open();
		this.socketChannel = socketChannel;
		this.outputQueue = new ArrayList<ByteBuffer>();
		this.rcvByteBuffer = ByteBuffer.wrap(rcvBuffer);
		this.expectedBytes = Integer.MAX_VALUE;
		this.sequence = 1;
		this.currentState = State.WAIT_FOR_CLIENT_RESPONSE;
		this.socketChannel.configureBlocking(false);
		this.selectionKey = socketChannel.register(selector, 0);
		this.sessionId = SESSION_ID.getAndAdd(1);
		this.shutdown = true;
		this.fieldTypeLibrary = fieldTypeLibrary;
	}
	
	public void close() 
	{
		shutdown = true;
		selector.wakeup();
	}
	
	public void start()
	{
		shutdown = false;
		new Thread(this, "FgSession").start();
	}

	public void sendMessageWithSession(ZapMessage message) throws IOException
	{
		sendMessageWithSessionId(message, sessionId);
	}
		
	public void sendMessageWithoutSession(ZapMessage message) throws IOException
	{
		sendMessageWithSessionId(message, 0);
	}

	private void sendMessageWithSessionId(ZapMessage message, int theSessionId)
	{
		byte[] trxBuffer = new byte[2048];
		ByteBuffer byteBuffer = ByteBuffer.wrap(trxBuffer);
		ZapSessionHeader header = new ZapSessionHeader((ZapMessageType)message.messageType(), theSessionId, sequence);
		sequence++;
		ZapPacket packet = new ZapPacket(header, message);

		byteBuffer.putShort((short) 0);
		packet.serialize(byteBuffer);
		int len = byteBuffer.position();
		trxBuffer[0] = (byte)((len-2 & 0xFF00) >> 8); 
		trxBuffer[1] = (byte)(len-2 & 0x00FF);
		byteBuffer.flip();
		
		synchronized (outputQueue)
		{
			outputQueue.add(byteBuffer);
		}
		selector.wakeup();
	}
	
	public void run()
	{
		try
		{
			queueServerChallenge();
			handleWrite();
			while (!shutdown)
				selectOnChannel();
			
			socketChannel.close();
			selector.close();
			
		} catch (ClosedChannelException e) 
		{
		    logger.info(socketChannel.socket().getInetAddress().toString() + " disconnected");
		    try
            {
                socketChannel.close();
            } catch (IOException e1)
            {
            }
		} catch (IOException e)
		{
			logger.info("FG connection closed");
		}
		notificationCenter.postNotification(Constants.FG_DISCONNECTED);
	}
	
	private void queueServerChallenge() throws IOException
	{
		byte[] buffer = new byte[2048];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		Random random = new Random(System.currentTimeMillis());
		byte[] serverSalt = new byte[ZapAuthenticationStrategy.SALT_SIZE];
		random.nextBytes(serverSalt);

		ServerChallenge serverChallenge = new ServerChallenge(serverSalt);

		ZapSessionHeader header = new ZapSessionHeader(serverChallenge.messageType());
		ZapPacket packet = new ZapPacket(header, serverChallenge);

		byteBuffer.putShort((short) 0);
		packet.serialize(byteBuffer);
		int len = byteBuffer.position();
		buffer[0] = (byte)((len-2 & 0xFF00) >> 8); 
		buffer[1] = (byte)(len-2 & 0x00FF); 
		
		byteBuffer.flip();
		synchronized (outputQueue)
		{
			outputQueue.add(byteBuffer);
		}
	}
	
	private void selectOnChannel() throws IOException
	{
		int interestedOps = SelectionKey.OP_READ;
		
		synchronized (outputQueue)
		{
			if (outputQueue.size() > 0)
				interestedOps |= SelectionKey.OP_WRITE;
		}
		
		selectionKey.interestOps(interestedOps);
		
		if (selector.select() > 0) 
		{
			if (selectionKey.isValid() && selectionKey.isWritable())
				handleWrite();
			
			if (selectionKey.isValid() && selectionKey.isReadable())
				handleRead();
		}
		selector.selectedKeys().clear();
	}
	
	private void handleWrite() throws IOException 
	{
		ByteBuffer nextBuffer = null;
		
		synchronized (outputQueue)
		{
			if (outputQueue.size() > 0) {
				nextBuffer = outputQueue.get(0);
				outputQueue.remove(0);
				socketChannel.write(nextBuffer);
				
				if (nextBuffer.hasRemaining()) 
				{
					outputQueue.add(0, nextBuffer);
				}
				else 
				{
					TransmissionPackage transmissionPackage = new TransmissionPackage(TYPE.BYTES_TRX, nextBuffer.array(), 0, nextBuffer.limit());
					notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);
				}
			}
		}	
	}
	
	private void expandRcvBuffer(Integer newSize)
	{
		byte[] tmpBuffer = new byte[newSize];
		ByteBuffer tmpByteBuffer = ByteBuffer.wrap(tmpBuffer);
		rcvByteBuffer.flip();
		tmpByteBuffer.put(rcvByteBuffer);
		rcvBuffer = tmpBuffer;
		rcvByteBuffer = tmpByteBuffer;		
	}
	
	private void handleRead() throws IOException
	{
		if (socketChannel.read(rcvByteBuffer) == -1)
			throw new ClosedChannelException();
		
		if (rcvByteBuffer.position() > 2 && expectedBytes == Integer.MAX_VALUE)
			expectedBytes = rcvBuffer[0] << 8 | rcvBuffer[1];
		
		if (expectedBytes > rcvByteBuffer.capacity())
			expandRcvBuffer(expectedBytes+2);
		
		if (rcvByteBuffer.position()-2 >= expectedBytes)
		{
			TransmissionPackage transmissionPackage = new TransmissionPackage(TYPE.BYTES_RCV, rcvBuffer, 0, rcvByteBuffer.position());
			notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);
			rcvByteBuffer.flip();
			handleMessage();
			rcvByteBuffer.clear();
			if (rcvBuffer.length > RCV_BUFFER_SIZE)
				expandRcvBuffer(RCV_BUFFER_SIZE);
			expectedBytes = Integer.MAX_VALUE;
		}
	}

	private void handleMessage() throws IOException 
	{
		switch (currentState) 
		{
			case WAIT_FOR_CLIENT_RESPONSE:
				sendAuthenticated();
				break;
				
			case AUTHENTICATED:
				ZapPacket packet = null;
				try {
					rcvByteBuffer.getShort();
					packet = ZapPacket.packetFromByteBuffer(rcvByteBuffer, fieldTypeLibrary);
					notificationCenter.postNotification(Constants.ZAP_MSG_RECEVIED, packet);
					expectedBytes = Integer.MAX_VALUE;
				} catch (Exception e)
				{
					logger.error("Unable to decode message", e);
				}
				break;
		}
	}
	
	private void sendAuthenticated() 
	{
		rcvByteBuffer.getShort();
		ZapPacket packet = ZapPacket.packetFromByteBuffer(rcvByteBuffer, fieldTypeLibrary);
		
		ChallengeResponseMessage challengeResponse = (ChallengeResponseMessage)packet.getMessage();
		
		ConnectionFlags connectionFlags = new ConnectionFlags();
		connectionFlags.setAuthorized(true);
		connectionFlags.setOutBoundDataPending(false);
		Integer serverTime = (int) (System.currentTimeMillis() / 1000L);
		AuthenticationResponseMessage response = new AuthenticationResponseMessage(connectionFlags, challengeResponse.getDeviceTime(), serverTime, null, null, null);

		ZapSessionHeader header = new ZapSessionHeader(response.messageType());
		packet = new ZapPacket(header, response);

		byte[] buffer = new byte[2048];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.clear();
		byteBuffer.putShort((short) 0);
		packet.serialize(byteBuffer);
		int len = byteBuffer.position();
		buffer[0] = (byte)((len-2 & 0xFF00) >> 8); 
		buffer[1] = (byte)(len-2 & 0x00FF); 
		byteBuffer.flip();

		synchronized (outputQueue)
		{
			outputQueue.add(byteBuffer);
		}
		notificationCenter.postNotification(Constants.FG_CONNECTED);
		currentState = State.AUTHENTICATED;
	}
}
