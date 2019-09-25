package zedi.pacbridge.net.tcp;

import zedi.pacbridge.net.ListenerStatus;

public class TcpListenerStatus implements ListenerStatus {

    private boolean listening;
    
    @Override
    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
