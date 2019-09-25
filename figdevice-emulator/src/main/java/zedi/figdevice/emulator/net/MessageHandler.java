package zedi.figdevice.emulator.net;

import zedi.pacbridge.net.Message;

public interface MessageHandler {
    public void handleMessage(Message message);
}
