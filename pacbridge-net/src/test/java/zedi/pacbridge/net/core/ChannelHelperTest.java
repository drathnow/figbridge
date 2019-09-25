package zedi.pacbridge.net.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.net.NetworkEventHandler;


public class ChannelHelperTest {

    private final static int OPERATION_MASK = SelectionKey.OP_READ;
    private AbstractSelectableChannel channel;
    private Selector selector;
    
    @Before
    public void setUp() throws Exception {
        selector = Selector.open();
        channel = SocketChannel.open();
    }

    @After
    public void tearDown() throws Exception {
        channel.close();
        selector.close();
    }
    
    @Test
    public void shouldForgetChannel() throws Exception {
        NetworkEventHandler key = mock(NetworkEventHandler.class);
        
        ChannelHelper dispatcher = new ChannelHelper(selector);
        
        dispatcher.registerChannel(channel, OPERATION_MASK);
        dispatcher.attach(channel, key);
        
        SelectionKey selectionKey = channel.keyFor(selector);
        assertNotNull(selectionKey);
        assertSame(key, selectionKey.attachment());
        
        dispatcher.forgetChannel(channel);
        assertNull(selectionKey.attachment());
        assertFalse(selectionKey.isValid());
    }
    
    @Test
    public void shouldRemoveChannelInterest() throws Exception {
        NetworkEventHandler key = mock(NetworkEventHandler.class);
        
        ChannelHelper dispatcher = new ChannelHelper(selector);
        dispatcher.registerChannel(channel, 0);

        dispatcher.addChannelInterest(channel, SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(0, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
        
        dispatcher.addChannelInterest(channel, SelectionKey.OP_READ);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_READ, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);

        dispatcher.removeChannelInterest(channel, SelectionKey.OP_READ);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(0, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
    }
    
    @Test
    public void shouldChangeChannelInterest() throws Exception {
        NetworkEventHandler eventHandler = mock(NetworkEventHandler.class);
        
        ChannelHelper dispatcher = new ChannelHelper(selector);
        dispatcher.registerChannel(channel, OPERATION_MASK);

        dispatcher.changeChannelInterest(channel, SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(0, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
        
        dispatcher.changeChannelInterest(channel, SelectionKey.OP_READ);
        assertEquals(0, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_READ, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
    }
    
    @Test
    public void shouldShouldAddChannelOperationsToExistingIterestedOperations() throws Exception {
        NetworkEventHandler eventHandler = mock(NetworkEventHandler.class);
        
        ChannelHelper dispatcher = new ChannelHelper(selector);
        dispatcher.registerChannel(channel, 0);

        dispatcher.addChannelInterest(channel, SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(0, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
        
        dispatcher.addChannelInterest(channel, SelectionKey.OP_READ);
        assertEquals(SelectionKey.OP_WRITE, channel.keyFor(selector).interestOps() & SelectionKey.OP_WRITE);
        assertEquals(SelectionKey.OP_READ, channel.keyFor(selector).interestOps() & SelectionKey.OP_READ);
    }
    
    @Test
    public void shouldRegisterChannel() throws Exception {
        ChannelHelper dispatcher = new ChannelHelper(selector);
        
        dispatcher.registerChannel(channel, OPERATION_MASK);

        SelectionKey selectionKey = channel.keyFor(selector);
        assertNotNull(selectionKey);
        assertEquals(OPERATION_MASK, selectionKey.interestOps());
    }    
}
