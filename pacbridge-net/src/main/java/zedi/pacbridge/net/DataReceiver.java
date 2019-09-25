package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public interface DataReceiver {
    public void handleReceivedData(ByteBuffer byteBuffer) throws ProtocolException; 
}
