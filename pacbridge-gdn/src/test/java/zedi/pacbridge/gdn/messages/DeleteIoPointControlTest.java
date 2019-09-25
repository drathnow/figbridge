package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class DeleteIoPointControlTest extends BaseTestCase {
    public static final Integer INDEX = 10;
    public static final Integer POLLSETID = 1;

    @Test
    public void testDeserialize() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put(POLLSETID.byteValue());
        byteBuffer.putShort(INDEX.shortValue());
        byteBuffer.flip();

        DeleteIoPointControl control = DeleteIoPointControl.deleteIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(POLLSETID, control.getPollSetNumber());
        assertEquals(INDEX, control.getIndex());
    }

    @Test
    public void testSerialize() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        DeleteIoPointControl deletePointControl = new DeleteIoPointControl(POLLSETID, INDEX);
        deletePointControl.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(POLLSETID.byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.intValue(), Unsigned.getUnsignedShort(byteBuffer));
    }
}
