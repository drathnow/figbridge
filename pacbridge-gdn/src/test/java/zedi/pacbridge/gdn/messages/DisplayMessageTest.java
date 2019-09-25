package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class DisplayMessageTest extends BaseTestCase {

    protected static final String DISPLAY_PACKET = "0a fe 00 73 68 0d 0a 54 49 6d 65 20 32 "
            + "31 3a 35 39 3a 32 32 20 0d 0a 44 41 74 65 20 31 31 2f 31 38 2f 30 33 20 0d 0a 50 41 43 20 20 0d 0a 20 20 20 52 50 54 "
            + "42 61 63 6b 20 20 4f 4e 20 0d 0a 20 20 20 52 50 54 4d 6f 64 65 20 20 31 30 20 0d 0a 20 20 20 4c 4f 57 70 6f 77 65 72 "
            + "20 4f 46 46 20 0d 0a 20 20 20 54 41 47 20 20 20 20 20 20 30 20 0d 0a 20 20 20 44 45 42 75 67 20 20 20 20 7c 52 54 55 "
            + "7c 0d 0a 20 20 20 54 49 4d 45 5a 6f 6e 65 20 30 20 0d 0a 20 20 20 41 57 41 4b 45 20 20 20 20 33 35 20 0d 0a 20 20 20 "
            + "41 52 43 68 69 76 65 20 20 4f 46 46 20 0d 0a 20 20 20 50 4f 57 20 20 20 20 20 20 30 20 0d 0a 20 20 20 4f 50 46 6c 61 "
            + "67 73 20 20 33 20 0d 0a 20 20 20 49 4f 20 20 20 20 20 20 20 0d 0a 20 20 20 20 20 20 49 50 44 65 6c 61 79 20 30 20 0d "
            + "0a 20 20 20 20 20 20 49 57 44 65 6c 61 79 20 30 20 0d 0a 20 20 20 20 20 20 42 4f 54 69 6d 65 72 20 30 20 0d 0a 20 20 "
            + "20 20 20 20 42 4f 43 6f 75 6e 74 20 30 20 0d 0a 20 20 20 44 43 20 20 20 20 20 20 20 0d 0a 20 20 20 20 20 20 44 45 42 "
            + "75 67 20 20 20 20 20 20 20 20 20 20 4f 46 46 20 0d 0a 20 20 20 20 20 20 53 45 4e 44 74 69 6d 65 6f 75 74 20 20 20 20 "
            + "30 20 0d 0a 20 20 20 20 20 20 43 4f 4e 4e 65 63 74 74 69 6d 65 6f 75 74 20 39 30 30 20 0d 0a 47 44 4e 20 20 0d 0a 20 "
            + "20 20 4d 53 47 20 0d 0a 20 20 20 20 20 20 4d 41 58 52 65 74 72 69 65 73 20 32 34 20 0d 0a 20 20 20 20 20 20 54 49 6d "
            + "65 6f 75 74 20 20 20 20 34 20 0d 0a 20 20 20 20 20 20 54 54 4c 20 20 20 20 20 20 20 20 35 34 30 20 0d 0a 52 54 55 20 "
            + "20 0d 0a 20 20 20 54 59 70 65 20 20 20 20 20 20 20 4d 42 50 0d 0a 20 20 20 44 45 62 75 67 20 20 20 20 20 20 ";

    @Test
    public void testFormatMessage() throws IOException {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(DISPLAY_PACKET);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        GdnPacket gdnPacket = GdnPacket.packetFromBuffer(byteBuffer);
        assertTrue(gdnPacket.getMessage() instanceof DisplayMessage);
    }    
}