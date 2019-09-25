package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;


abstract class FadMessage {
    public abstract boolean isControlMessage();
    public abstract void transmitThroughMessageTransmitter(FadMessageTransmitter messageTransmitter, ByteBuffer byteBuffer) throws IOException;
    public abstract int size();
}