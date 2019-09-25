package zedi.pacbridge.stp.fad;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.ProtocolException;

interface FadDataHandler {
    public void handleData(ByteBuffer byteBuffer) throws ProtocolException; 
}
