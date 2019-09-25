package zedi.pacbridge.net.core;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

import zedi.pacbridge.net.NetworkEventHandler;

public interface ChannelManager {
    public void registerChannel(SelectableChannel channel, int operationMask) throws IOException;
    public void attach(SelectableChannel channel, NetworkEventHandler eventHandler) throws IOException;
    public void addChannelInterest(SelectableChannel channel, int addOperationMask) throws IOException;
    public void changeChannelInterest(SelectableChannel channel, int newOperationMask) throws IOException;
    public void removeChannelInterest(SelectableChannel channel, int removeOperationMask) throws IOException;
    public void forgetChannel(SelectableChannel channel) throws IOException;
}
