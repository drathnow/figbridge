package zedi.pacbridge.stp.fad;

import java.io.IOException;

interface FadMessageHandler {
    public void handleMessage(FadMessage fadMessage) throws IOException;
}
