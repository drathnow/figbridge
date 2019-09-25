package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface FadMessageTransmitter {
    public void transmitByteBuffer(ByteBuffer byteBuffer) throws IOException;
}
