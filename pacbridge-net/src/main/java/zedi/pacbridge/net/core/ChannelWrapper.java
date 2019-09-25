package zedi.pacbridge.net.core;

import java.io.IOException;
import java.nio.channels.SelectableChannel;


public interface ChannelWrapper {
    public abstract void configureBlocking(boolean shouldBlock) throws IOException;
    public abstract void close() throws IOException;
    SelectableChannel getChannel();
}