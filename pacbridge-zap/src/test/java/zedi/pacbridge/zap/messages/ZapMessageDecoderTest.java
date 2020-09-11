package zedi.pacbridge.zap.messages;

import static org.junit.Assert.*;

import org.junit.Test;

public class ZapMessageDecoderTest
{

    private static final byte[] BYTES = {0x00, 0x12, 0x01, (byte)0xFF, (byte)0xFF, 0x00, 0x00, 0x00, 0x02, 0x00, 0x07, 0x01, 0x00, 0x0B, 0x00, 0x09, 0x00, 0x02, 0x09, 0x01};
    @Test
    public void shouldDecodeBytes()
    {
        ZapMessageDecoder messageDecoder = new ZapMessageDecoder();
        messageDecoder.decodePacketBytes(BYTES);
    }
}
