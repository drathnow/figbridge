package zedi.pacbridge.stp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import zedi.pacbridge.net.ProtocolDecoder;
import zedi.pacbridge.stp.apl.AplDecoder;
import zedi.pacbridge.stp.fad.FadDecoder;
import zedi.pacbridge.utl.crc.CheckSumException;
import zedi.pacbridge.utl.crc.CrcException;

public class StpDecoder implements ProtocolDecoder {
    private AplDecoder aplDecoder;
    private FadDecoder fadDecoder;

    public StpDecoder() {
        this.aplDecoder = new AplDecoder();
        this.fadDecoder = new FadDecoder();
    }
    
    public void addBytes(byte[] bytes) throws CheckSumException {
        aplDecoder.decodeBytesFromByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
    }

    public byte[] nextMessage() throws IOException, CrcException {
        byte[] bytes = aplDecoder.nextMessage();
        if (bytes != null)
            return fadDecoder.payloadForFadMessage(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
        return null;
    }
}
