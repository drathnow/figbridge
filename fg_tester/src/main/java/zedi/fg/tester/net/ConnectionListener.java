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
    private boolean restart;
    private FieldTypeLibrary fieldTypeLibrary;

    public ConnectionListener(NotificationCenter notificationCenter, String address, Integer port, FieldTypeLibrary fieldTypeLibrary) throws IOException
    {
        this.notificationCenter = notificationCenter;
        this.socketAddress = new InetSocketAddress(address, port);
        this.selector = Selector.open();
        this.shutdown = true;
        this.restart = false;
        this.fieldTypeLibrary = fieldTypeLibrary;
    }

    public void start()
    {
        shutdown = false;
        restart = false;
        Thread thread = new Thread(this, "ConnectionListener");
        thread.setDaemon(true);
        thread.start();
    }

    public void setListenerAddressAndPort(String address, Integer port)
    {
        this.socketAddress = new InetSocketAddress(address, port);
        selector.wakeup();
    }
    
    public void close()
    {
        shutdown = true;
        restart = true;
        if (currentSession != null)
            currentSession.close();
        selector.wakeup();
    }

    public void run()
    {
        try
        {
            while (!shutdown)
            {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false); 
                logger.info("Binding listener socket to " + socketAddress.toString());
                serverSocketChannel.bind(socketAddress);
                selectionKey = serverSocketChannel.register(selector, serverSocketChannel.validOps(), null);

                logger.info("Listening for connections on " + socketAddress.toString());
                while (!restart)
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
                
                selectionKey.interestOps(0);
                selectionKey.cancel();
                serverSocketChannel.close();
                restart = false;
            }

        } catch (IOException e)
        {
            if (selector.isOpen()) {
                logger.error("Unable to accept", e);
            }
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
