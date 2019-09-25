package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class ConfigureUpdateMessageTest extends BaseTestCase {
    
    private static final String CONFIG_UPDATE_PACKET_STRING = "01 02 00 01 02 00 08 20 02 00 D7 70 03 00 08 61 73 64 66 61 73 64 66 10 11 01 10 30 01 10 31 01 10 32 02 10 33 01 10 34 01";
    private TestingFieldTypeLibrary fieldTypeLibrary;
    private static final String PKT = "01 00 0B 00 00 00 01 01 02 00 01 02 00 08 20 02 00 D7 70 03 00 08 61 73 64 66 61 73 64 66 10 11 01 10 30 01 10 31 01 10 32 02 10 33 01 10 34 01";

    @Before
    public void setup() {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        fieldTypeLibrary = new TestingFieldTypeLibrary(inputStream);
    }

    @Ignore
    @Test
    public void shouldDeserializePacketFromByteStream() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ZapPacket packet = ZapPacket.packetFromByteBuffer(byteBuffer, fieldTypeLibrary);
        assertNotNull(packet);
        ConfigureUpdateMessage message = (ConfigureUpdateMessage)packet.getMessage();
        assertNotNull(message);
    }

    @Test
    public void shouldDeserializeMessageFromByteStream() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(CONFIG_UPDATE_PACKET_STRING);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        ConfigureUpdateMessage message = ConfigureUpdateMessage.configureUpdateMessageFromByteBuffer(byteBuffer, fieldTypeLibrary);
        assertNotNull(message);
        
        List<Action> actions = message.getActions();
        assertEquals(1, actions.size());
        Action action = actions.get(0);
        List<Field<?>> fields = action.getFields();
        assertEquals(8, fields.size());
        
        Field<?> field = fields.get(0);
        System.out.println("Field  Type: " + field.getFieldType());
        System.out.println("Field Value: " + field.getValue());
    }

}
