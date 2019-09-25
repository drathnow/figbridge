package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;


public interface DataTransmitter {
    public abstract void transmitData(ByteBuffer byteBuffer) throws IOException;
    public void close();
}