package zedi.pacbridge.net.core;

import java.io.IOException;

import zedi.pacbridge.net.NetworkEventHandler;

public class ChannelManagerDispatcherKey implements DispatcherKey {
    private long registrationTime;
    private ChannelManager channelManager;
    
    ChannelManagerDispatcherKey(ChannelManager channelManager) {
        this.channelManager = channelManager;
        this.registrationTime = System.currentTimeMillis();
    }

    long getRegistrationTime() {
        return registrationTime;
    }

    public void registerChannel(ChannelWrapper channel) throws IOException {
        channelManager.registerChannel(channel.getChannel(), 0);
    }

    public void attach(ChannelWrapper channel, NetworkEventHandler eventHandler) throws IOException {
        channelManager.attach(channel.getChannel(), eventHandler);
    }
    
    public void addChannelInterest(ChannelWrapper channel, int addOperationMask) throws IOException {
        channelManager.addChannelInterest(channel.getChannel(), addOperationMask);
    }

    public void changeChannelInterest(ChannelWrapper channel, int newOperationMask) throws IOException {
        channelManager.changeChannelInterest(channel.getChannel(), newOperationMask);
    }

    public void removeChannelInterest(ChannelWrapper channel, int removeOperationMask) throws IOException {
        channelManager.removeChannelInterest(channel.getChannel(), removeOperationMask);
    }

    public void forgetChannel(ChannelWrapper channel) throws IOException {
        channelManager.forgetChannel(channel.getChannel());
    }
}
