package zedi.pacbridge.net.core;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;

import zedi.pacbridge.net.NetworkEventHandler;

class ChannelHelper implements ChannelManager {

    private Selector selector;
    private AtomicInteger registeredChannelCount = new AtomicInteger(0);
    
    public ChannelHelper(Selector selector) {
        this.selector = selector;
    }
    
    @Override
    public void registerChannel(SelectableChannel channel, int operationMask) throws IOException {
        if (channel.isRegistered()) {
            if (channel.keyFor(selector) == null)
                throw new IllegalArgumentException("Attempting to register channel that is already registered with another selector");
        } else {
            if ((operationMask & SelectionKey.OP_ACCEPT) != 0)
                throw new IllegalArgumentException("You cannot register a channel for accept operations");
            channel.configureBlocking(false);
            registeredChannelCount.incrementAndGet();
            channel.register(selector, operationMask);
        }
    }
    
    @Override
    public void attach(SelectableChannel channel, NetworkEventHandler eventHandler) throws IOException {
        SelectionKey key;
        if ((key = channel.keyFor(selector)) == null)
            throw new IllegalArgumentException("Attempting to attache an event handler to an unregistered channel");
        key.attach(eventHandler);
    }
    
    @Override
    public void changeChannelInterest(SelectableChannel channel, int newOperationMask) throws IOException {
        SelectionKey selectionKey = channel.keyFor(selector);
        if (selectionKey != null)
            selectionKey.interestOps(newOperationMask);        
    }
    
    @Override
    public void addChannelInterest(SelectableChannel channel, int addOperationMask) throws IOException {
        SelectionKey selectionKey = channel.keyFor(selector);
        if (selectionKey != null)
            selectionKey.interestOps(selectionKey.interestOps() | addOperationMask);
    }

    @Override
    public void removeChannelInterest(SelectableChannel channel, int removeOperationMask) throws IOException {
        SelectionKey selectionKey = channel.keyFor(selector);
        if (selectionKey != null)
            selectionKey.interestOps(selectionKey.interestOps() & ~removeOperationMask);
    }
    
    @Override
    public void forgetChannel(SelectableChannel channel) {
        registeredChannelCount.decrementAndGet();
        SelectionKey selectionKey = channel.keyFor(selector);
        if (selectionKey != null && selectionKey.isValid()) {
            selectionKey.interestOps(0);
            selectionKey.attach(null);
            selectionKey.cancel();
        }
    }
    
}
