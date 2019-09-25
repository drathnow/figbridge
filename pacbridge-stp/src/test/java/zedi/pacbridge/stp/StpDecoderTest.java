package zedi.pacbridge.stp;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class StpDecoderTest extends BaseTestCase {

    private static final String BYTES = "1B 02 C0 29 E1 46 0A 26 01 4B 15 2B 51 09 20 01 00 01 00 00 1B 04 00 1B 05 00 00 1B 05 00 1B 04 00 00 04 00 07 00 00 00 00 00 05 00 06 00 07 00 00 00 06 00 05 00 00 00 07 00 04 00 00 00 08 00 08 00 00 00 00 00 09 00 07 00 00 00 00 00 5B 1B 03";
    
    @Test
    public void shouldDecodeBytes() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(BYTES);
        StpDecoder decoder = new StpDecoder();
        decoder.addBytes(bytes);
        byte[] message = decoder.nextMessage();
        assertNotNull(message);
    }
}
