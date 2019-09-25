package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.reporting.ResponseStatus;

public class ConfigureUpdateAckDetailsTest extends BaseTestCase {

    @Test
    public void shouldSerializeFullAckMessage() throws Exception {
        byte[] buffer = new byte[100];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        ConfigureUpdateAckDetails details = new ConfigureUpdateAckDetails(ResponseStatus.OK);
        AckMessage ackMessage = new AckMessage(1, ZapMessageType.ConfigureUpdate, details);
        ackMessage.serialize(byteBuffer);
    }

    
    @Test
    public void shouldSerializeDetails() throws Exception {
        ConfigureUpdateAckDetails details = new ConfigureUpdateAckDetails(ResponseStatus.OK);
        byte[] bytes = details.asBytes();
        assertEquals(1, bytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        assertEquals(ResponseStatus.OK.getNumber().byteValue(), byteBuffer.get());
    }
}
