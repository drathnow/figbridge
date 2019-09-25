package zedi.pacbridge.net.core;

import java.nio.channels.Selector;

public class ChannelHelperFactory {

    public ChannelHelper newChannelHelperWithSelector(Selector selector) {
        return new ChannelHelper(selector);
    }
}
