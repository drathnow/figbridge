package zedi.fg.tester.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;

import zedi.fg.tester.util.Constants;
import zedi.fg.tester.util.TransmissionPackage;
import zedi.fg.tester.util.TransmissionPackage.TYPE;
import zedi.pacbridge.app.auth.zap.ZapAuthenticationStrategy;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.AuthenticationResponseMessage;
import zedi.pacbridge.zap.messages.ChallengeResponseMessage;
import zedi.pacbridge.zap.messages.ConnectionFlags;
import zedi.pacbridge.zap.messages.ServerChallenge;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapSessionHeader;

public class FgAuthenticator
{
	private NotificationCenter notificationCenter;

	public FgAuthenticator(NotificationCenter notificationCenter)
	{
		this.notificationCenter = notificationCenter;
	}

	public void authenticate(Selector selector, SocketChannel socketChannel) throws IOException 
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
		while (byteBuffer.hasRemaining())
		{
			socketChannel.write(byteBuffer);
		}
		TransmissionPackage transmissionPackage = new TransmissionPackage(TYPE.BYTES_TRX, byteBuffer.array(), 0, byteBuffer.limit());
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);
		
		int expectedLength = 0x7fffffff;
		int byteReceived = 0;
		byteBuffer.clear();
		
		socketChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
		selector.select();
		
		while (byteReceived < expectedLength)
		{
			len = socketChannel.read(byteBuffer);
			if (len == -1)
				throw new ClosedChannelException();
			byteReceived += len;
			if (byteReceived > 2)
			{
				if (expectedLength == 0x7fffffff)
					expectedLength = buffer[0] << 8 | buffer[1];
			}
		}
		
		transmissionPackage = new TransmissionPackage(TYPE.BYTES_RCV, byteBuffer.array(), 0, byteReceived);
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);

		byteBuffer.flip();
		byteBuffer.getShort();
		packet = ZapPacket.packetFromByteBuffer(byteBuffer);
		
		ChallengeResponseMessage challengeResponse = (ChallengeResponseMessage)packet.getMessage();
		
		ConnectionFlags connectionFlags = new ConnectionFlags();
		connectionFlags.setAuthorized(true);
		connectionFlags.setOutBoundDataPending(false);
		Integer serverTime = (int) (System.currentTimeMillis() / 1000L);
		AuthenticationResponseMessage response = new AuthenticationResponseMessage(connectionFlags, challengeResponse.getDeviceTime(), serverTime, null, null, null);

		header = new ZapSessionHeader(response.messageType());
		packet = new ZapPacket(header, response);

		byteBuffer.clear();
		byteBuffer.putShort((short) 0);
		packet.serialize(byteBuffer);
		len = byteBuffer.position();
		buffer[0] = (byte)((len-2 & 0xFF00) >> 8); 
		buffer[1] = (byte)(len-2 & 0x00FF); 

		byteBuffer.flip();
		while (byteBuffer.hasRemaining())
		{
			socketChannel.write(byteBuffer);
		}
		transmissionPackage = new TransmissionPackage(TYPE.BYTES_TRX, byteBuffer.array(), 0, byteBuffer.position());
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);
	}
	
	public void authenticate(InputStream inputStream, OutputStream outputStream) throws IOException
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

		outputStream.write(byteBuffer.array(), 0, byteBuffer.position());
		TransmissionPackage transmissionPackage = new TransmissionPackage(TYPE.BYTES_TRX, byteBuffer.array(), 0, byteBuffer.position());
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);

		len = 0;
		int expectedLength = 0x7fffffff;
		int byteReceived = 0;
		byteBuffer.clear();
		while (byteReceived < expectedLength)
		{
			len = inputStream.read(buffer, len, buffer.length - len);
			byteReceived += len;
			if (byteReceived > 2)
			{
				if (expectedLength == 0x7fffffff)
					expectedLength = buffer[0] << 8 | buffer[1];
			}
		}

		transmissionPackage = new TransmissionPackage(TYPE.BYTES_RCV, byteBuffer.array(), 0, byteReceived);
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);

		byteBuffer.position(byteReceived);
		byteBuffer.flip();
		byteBuffer.getShort();
		packet = ZapPacket.packetFromByteBuffer(byteBuffer);
		
		ChallengeResponseMessage challengeResponse = (ChallengeResponseMessage)packet.getMessage();
		
		ConnectionFlags connectionFlags = new ConnectionFlags();
		connectionFlags.setAuthorized(true);
		connectionFlags.setOutBoundDataPending(false);
		Integer serverTime = (int) (System.currentTimeMillis() / 1000L);
		AuthenticationResponseMessage response = new AuthenticationResponseMessage(connectionFlags, challengeResponse.getDeviceTime(), serverTime, null, null, null);

		header = new ZapSessionHeader(response.messageType());
		packet = new ZapPacket(header, response);

		byteBuffer.clear();
		byteBuffer.putShort((short) 0);
		packet.serialize(byteBuffer);
		len = byteBuffer.position();
		buffer[0] = (byte)((len-2 & 0xFF00) >> 8); 
		buffer[1] = (byte)(len-2 & 0x00FF); 

		outputStream.write(byteBuffer.array(), 0, byteBuffer.position());
		transmissionPackage = new TransmissionPackage(TYPE.BYTES_TRX, byteBuffer.array(), 0, byteBuffer.position());
		notificationCenter.postNotification(Constants.TRANSMISSION_NOTIFICATION_NAME, transmissionPackage);
	}

}
