package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.zap.ZapMessageType;

public class MessageFactoryTest extends BaseTestCase {
    private static final String PKT = "01 00 04 00 00 00 02 01 00 02 00 01 00 00 00 00 00 00 00 00 00 00 00 0a 01 00 00 00 00 00 00 00 00 00 00 00 0a 53 f3 c5 56 00 05 00 01 00 00 0a 00 00 00 6e 01 00 00 00 6f 08 00 00 01 f6 07 00 00 01 f5 08 00 00 02 59 07 53 f3 c5 56 00 01 00 3f 80 00 00 05 00 00 00 00 05 00";
    
    @Test
    public void shouldDecodeBytes() throws Exception {
        FieldTypeLibrary typeLibrary = mock(FieldTypeLibrary.class);
        ZapMessageFactory messageFactory = new ZapMessageFactory(typeLibrary);
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(PKT));
        Message message = messageFactory.messageFromByteBuffer(ZapMessageType.BundledReport.getNumber(), byteBuffer);
        assertNotNull(message);
    }
}
