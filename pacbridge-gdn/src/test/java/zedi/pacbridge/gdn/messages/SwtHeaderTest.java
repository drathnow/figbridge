package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class SwtHeaderTest extends BaseTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testHeaderForVersion() {
        assertTrue(SwtHeader.headerForHeaderType(SwtHeaderType.Header10) instanceof SwtHeader10);
        assertTrue(SwtHeader.headerForHeaderType(SwtHeaderType.Header12) instanceof SwtHeader12);
    }
    
    @Test
    public void shouldReturnHeaderFromByteBuffers() throws Exception {
        SwtHeader header = SwtHeader.headerFromByteBuffer(ByteBuffer.wrap(new byte[] {0x0a, 0x01}));
        assertTrue(header instanceof SwtHeader10);
        assertEquals(GdnMessageType.AddIoPointMessage, header.messageType());
        header = SwtHeader.headerFromByteBuffer(ByteBuffer.wrap(new byte[] {0x0c, 0x01, 0x02, 0x03}));
        assertTrue(header instanceof SwtHeader12);
        assertEquals(GdnMessageType.AddIoPointMessage, header.messageType());
        assertEquals(0x0203, ((SwtHeader12)header).getSessionId().intValue());
    }
}
