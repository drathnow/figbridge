package zedi.pacbridge.net.tcp;

import javax.mail.event.TransportAdapter;

public class TansportAdapterEventNotifier {
    private TransportAdapter transportAdapter;
    
    public TansportAdapterEventNotifier(TransportAdapter transportAdapter, Object listener) {
        this.transportAdapter = transportAdapter;
    }
}
