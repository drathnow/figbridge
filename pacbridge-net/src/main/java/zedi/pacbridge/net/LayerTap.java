package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public interface LayerTap {
    public void bytesSent(ByteBuffer byteBuffer);
    public void bytesReceived(ByteBuffer byteBuffer);
}
