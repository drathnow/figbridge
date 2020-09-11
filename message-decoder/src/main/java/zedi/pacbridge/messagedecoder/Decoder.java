package zedi.pacbridge.messagedecoder;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.gdn.messages.GdnMessageDecoder;
import zedi.pacbridge.stp.StpDecoder;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;

public class Decoder
{
    private static final Logger logger = LoggerFactory.getLogger(Decoder.class.getName());

    private ZapMessageDecoder zapMessageDecoder = null;
    private GdnMessageDecoder gdnMessageDecoder = null;

    public Decoder(FieldTypeLibrary fieldTypeLibrary)
    {
        zapMessageDecoder = new ZapMessageDecoder(fieldTypeLibrary);
        gdnMessageDecoder = new GdnMessageDecoder();
    }

    public void decodeString(String hexString)
    {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(hexString);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        try
        {
            if (isStp(byteBuffer))
            {
                StpDecoder stpDecoder = new StpDecoder();
                stpDecoder.addBytes(bytes);
                byte[] nextMessageBytes = null;
                
                while ((nextMessageBytes = stpDecoder.nextMessage()) != null)
                    logger.info(gdnMessageDecoder.decodedMessage(nextMessageBytes));
            }
            else if (isGdn(byteBuffer))
                logger.info(gdnMessageDecoder.decodedMessage(bytes));
            else
                logger.info(zapMessageDecoder.decodePacketBytes(byteBuffer));
        } catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Unable to decode bytes: " + e.toString());
            logger.error("Bytes: " + hexString);
        }
    }

    private boolean isGdn(ByteBuffer byteBuffer)
    {
        return Unsigned.getUnsignedShort(byteBuffer, 0) != byteBuffer.limit();
    }
    
    private boolean isStp(ByteBuffer byteBuffer)
    {
        if (byteBuffer.get(0) == 0x1b && byteBuffer.get(1) == 0x02)
            return byteBuffer.get(byteBuffer.limit() - 2) == 0x1b && byteBuffer.get(byteBuffer.limit() - 1) == 0x03;
        return false;
    }
}
