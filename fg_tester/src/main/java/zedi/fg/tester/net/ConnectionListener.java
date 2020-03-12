package zedi.fg.tester.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import zedi.fg.tester.util.FgMessageSender;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ZapMessage;

public class ConnectionListener implements Runnable, FgMessageSender
{
	private static final Logger logger = Logger.getLogger(ConnectionListener.class);

	private NotificationCenter notificationCenter;
	private InetSocketAddress socketAddress;
	private FgSession currentSession;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private SelectionKey selectionKey;
	private boolean shutdown;
	private FieldTypeLibrary fieldTypeLibrary;
	
	public ConnectionListener(NotificationCenter notificationCenter, String address, Integer port, FieldTypeLibrary fieldTypeLibrary) throws IOException
	{
		this.notificationCenter = notificationCenter;
		this.socketAddress = new InetSocketAddress(address, port);
		this.selector = Selector.open();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.bind(socketAddress);
		this.serverSocketChannel.configureBlocking(false);
		this.selectionKey = serverSocketChannel.register(selector, serverSocketChannel.validOps(), null);
		this.shutdown = true;
		this.fieldTypeLibrary = fieldTypeLibrary;
	}
	
	public void start()
	{
		shutdown = false;
		new Thread(this, "ConnectionListener").start();
	}
	
	public void close()
	{
		shutdown = true;
		if (currentSession != null)
			currentSession.close();
		selector.wakeup();
	}
	
	public void run()
	{
		try
		{
			logger.info("Listening for connections on " + socketAddress.toString());
			while (!shutdown) 
			{
				if (selector.select() > 0)
				{
					selectionKey.interestOps(SelectionKey.OP_ACCEPT);
		            if (selectionKey.isValid() && selectionKey.isAcceptable()) 
		            {
			            if (currentSession != null)
			            	currentSession.close();
			            SocketChannel socketChannel = serverSocketChannel.accept();
			            logger.info("New connection accepted from " + socketChannel.socket().getInetAddress().toString());
			            currentSession = new FgSession(notificationCenter, socketChannel, fieldTypeLibrary);
			            currentSession.start();
		            }
				}
				selector.selectedKeys().clear();
			}
		} 
		catch (IOException e)
		{
			if (selector.isOpen())
				logger.error("Unable to accept", e);
		}
		
		if (currentSession != null)
			currentSession.close();
		currentSession = null;
	}
	
	@Override
	public void sendMessageWithoutSession(ZapMessage message) throws IOException
	{
		if (currentSession != null)
			currentSession.sendMessageWithoutSession(message);
	}

	@Override
	public void sendMessageWithSession(ZapMessage message) throws IOException
	{
		if (currentSession != null)
			currentSession.sendMessageWithSession(message);
	}
}
