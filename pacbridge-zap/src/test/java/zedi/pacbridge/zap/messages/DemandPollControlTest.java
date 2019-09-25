package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.Zap;

public class DemandPollControlTest extends BaseTestCase {
    private static final Long EVENT_ID = 8989L;
    private static final Long INDEX = 1234L;
    private static final Integer POLLSET = 6789;

    @Test
    public void shouldSerializeWithIndex() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        DemandPollControl control = new DemandPollControl(EVENT_ID, Zap.MAX_INDEX, 0);
        control.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(EVENT_ID.longValue(), byteBuffer.getLong());
        assertEquals(0, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(Zap.MAX_INDEX.longValue(), Unsigned.getUnsignedInt(byteBuffer));
    }
    
    @Test
    public void shouldSerializeWithPollset() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        DemandPollControl control = new DemandPollControl(EVENT_ID, 0L, Zap.MAX_POLLSET_NUMBER);
        control.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(EVENT_ID.longValue(), byteBuffer.getLong());
        assertEquals(Zap.MAX_POLLSET_NUMBER.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(0L, Unsigned.getUnsignedInt(byteBuffer));
    }

    @Test
    public void shouldDeserializeMaxIndexControl() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        byteBuffer.putLong(EVENT_ID.longValue());
        byteBuffer.putShort((short)0);
        byteBuffer.putInt(Zap.MAX_INDEX.intValue());
        byteBuffer.flip();
        DemandPollControl control = DemandPollControl.messageFromByteBuffer(byteBuffer);
        
        assertEquals(EVENT_ID.longValue(), control.getEventId().longValue());
        assertEquals(0, control.getPollSetNumber().intValue());
        assertEquals(Zap.MAX_INDEX.longValue(), control.getIndex().longValue());
    }
    
    @Test
    public void shouldDeserializeIndexControl() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        byteBuffer.putLong(EVENT_ID.longValue());
        byteBuffer.putShort((short)0);
        byteBuffer.putInt(INDEX.intValue());
        byteBuffer.flip();
        DemandPollControl control = DemandPollControl.messageFromByteBuffer(byteBuffer);
        
        assertEquals(EVENT_ID.longValue(), control.getEventId().longValue());
        assertEquals(0, control.getPollSetNumber().intValue());
        assertEquals(INDEX.longValue(), control.getIndex().longValue());
    }
    
    @Test
    public void shouldDeserializePollsetControl() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DemandPollControl.SIZE);
        byteBuffer.putLong(EVENT_ID.longValue());
        byteBuffer.putShort(POLLSET.shortValue());
        byteBuffer.putInt(0);
        byteBuffer.flip();
        DemandPollControl control = DemandPollControl.messageFromByteBuffer(byteBuffer);
        
        assertEquals(POLLSET, control.getPollSetNumber());
        assertEquals(0L, control.getIndex().longValue());
    }

    @Test(expected = RuntimeException.class)
    public void shouldCheckRangeOfPollset() throws Exception {
        Integer tooLargePollset= Zap.MAX_POLLSET_NUMBER+1;
        new DemandPollControl(EVENT_ID, 0L, tooLargePollset);
    }
    
    @Test(expected = RuntimeException.class)
    public void shouldCheckRangeOfIndex() throws Exception {
        Long tooLargeIndex = Zap.MAX_INDEX+1;
        new DemandPollControl(EVENT_ID, tooLargeIndex, 0);
    }
    
    @Test(expected = RuntimeException.class)
    public void shouldNotAllowNegativeValueForPollset() throws Exception {
        new DemandPollControl(EVENT_ID, 0L, -1);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAllowNegativeValueForIndex() throws Exception {
        new DemandPollControl(EVENT_ID, -1L, 0);
    }
}
