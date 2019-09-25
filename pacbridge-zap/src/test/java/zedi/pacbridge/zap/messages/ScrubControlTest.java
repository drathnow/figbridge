package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ScrubControlTest extends BaseTestCase {

    private static final Long EVENT_ID = 234L;
    
    @Test
    public void shouldReturnStaticSize() throws Exception {
        ScrubControl control = new ScrubControl(EVENT_ID, ScrubControl.MSG_SCRUB_ALL);
        assertEquals(ScrubControl.SIZE, control.size());
    }
    
    @Test
    public void shouldSerializeAllOptions() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        ScrubControl control = new ScrubControl(EVENT_ID, ScrubControl.MSG_SCRUB_ALL);
        
        control.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(ScrubControl.SIZE.intValue(), byteBuffer.limit());
        assertEquals(ScrubControl.MSG_SCRUB_ALL.intValue(), byteBuffer.getShort());
    }
}
