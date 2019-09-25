package zedi.pacbridge.net.core;

import java.io.IOException;

import zedi.pacbridge.net.NetworkEventHandler;

public interface DispatcherKey {
    public void registerChannel(ChannelWrapper channel) throws IOException;
    public void attach(ChannelWrapper channel, NetworkEventHandler eventHandler) throws IOException;
    public void addChannelInterest(ChannelWrapper channel, int addOperationMask) throws IOException;
    public void changeChannelInterest(ChannelWrapper channel, int newOperationMask) throws IOException;
    public void removeChannelInterest(ChannelWrapper channel, int removeOperationMask) throws IOException;
    public void forgetChannel(ChannelWrapper channel) throws IOException;
}