package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DemandPollControlTest extends BaseTestCase {
    public static final Integer INDEX = 10;
    public static final Integer POLLSET_NUMBER = 12;
    private static final byte[] DEMAND_POLL_BYTES = new byte[]{0x0c, 0x00, 0x0a};
    
    @Test
    public void shouldSerializeToByteBUffer() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        DemandPollControl control = new DemandPollControl(INDEX, POLLSET_NUMBER);
        control.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(POLLSET_NUMBER.intValue(), byteBuffer.get());
        assertEquals(INDEX.byteValue(), byteBuffer.getShort());
    }

    @Test
    public void shouldSerializeFromByteBuffer() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(DEMAND_POLL_BYTES);
        DemandPollControl control = DemandPollControl.demandPollControlFromByteBuffer(byteBuffer);
        assertEquals(INDEX, control.getIndex());
        assertEquals(POLLSET_NUMBER, control.getPollSetNumber());
    }
}
