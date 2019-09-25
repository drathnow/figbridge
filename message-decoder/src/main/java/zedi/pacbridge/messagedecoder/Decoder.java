package zedi.pacbridge.messagedecoder;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;


public class Decoder {
    private static final Logger logger = LoggerFactory.getLogger(Decoder.class.getName());

    private ZapMessageDecoder zapMessageDecoder = new ZapMessageDecoder();

    public Decoder() {
    }

    public void decodeString(String hexString) {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(hexString);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.mark();
        final int size = Unsigned.getUnsignedShort(byteBuffer);
        if (size != bytes.length + (Short.SIZE / 8))
            byteBuffer.reset();
        try {
            byteBuffer.reset();
            logger.info(zapMessageDecoder.decodePacketBytes(byteBuffer));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to decode bytes: " + e.toString());
            logger.error("Bytes: " + hexString);
        }
    }
}
