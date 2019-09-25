package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ServerTimeAckDetailsTest extends BaseTestCase {

    private static final Integer DEVICE_TIME = 100;
    private static final Integer SERVER_TIME = 200;
    
    @Test
    public void shouldReturnBytes() throws Exception {
        ServerTimeAckDetails details = new ServerTimeAckDetails(DEVICE_TIME, SERVER_TIME);
        ByteBuffer byteBuffer = ByteBuffer.wrap(details.asBytes());
        
        assertEquals(DEVICE_TIME.intValue(), byteBuffer.getInt());
        assertEquals(SERVER_TIME.intValue(), byteBuffer.getInt());
    }

    @Test
    public void shouldSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        ServerTimeAckDetails details = new ServerTimeAckDetails(DEVICE_TIME, SERVER_TIME);
        details.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(ServerTimeAckDetails.SIZE.shortValue()+1, byteBuffer.getShort());
        assertEquals(AckDetailsType.ServerTime.getNumber().byteValue(), byteBuffer.get());
        assertEquals(DEVICE_TIME.intValue(), byteBuffer.getInt());
        assertEquals(SERVER_TIME.intValue(), byteBuffer.getInt());
    }
}
