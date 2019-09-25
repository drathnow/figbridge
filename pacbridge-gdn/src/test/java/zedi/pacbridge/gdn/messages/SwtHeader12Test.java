package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class SwtHeader12Test extends BaseTestCase {
    protected static final byte HEADER12[] = new byte[]{0x0c, 0x09, 0x00, 0x01};
    protected static final GdnMessageType TEST_MSG_TYPE = GdnMessageType.DemandPoll;
    private static final int SESSION_ID = 22; 
    protected SwtHeader12 swtHeader12;
    protected ByteArrayInputStream byteArrayInputStream;
    protected DataInputStream dataInputStream;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        swtHeader12 = new SwtHeader12();
        byteArrayInputStream = new ByteArrayInputStream(HEADER12);
        dataInputStream = new DataInputStream(byteArrayInputStream);
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @After
    public void tearDown() throws Exception {
        swtHeader12 = null;
        super.tearDown();
    }

    @Test
    public void testHeaderForHeaderType() {
        assertEquals(SwtHeader12.class, SwtHeader.headerForHeaderType(SwtHeaderType.Header12).getClass());
        assertEquals(SwtHeader10.class, SwtHeader.headerForHeaderType(SwtHeaderType.Header10).getClass());
    }

    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        SwtHeader12 header12 = new SwtHeader12(SESSION_ID, TEST_MSG_TYPE);
        header12.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(SwtHeaderType.Header12.getTypeNumber().intValue(), byteBuffer.get());
        assertEquals(TEST_MSG_TYPE.getNumber().intValue(), byteBuffer.get());
        assertEquals(SESSION_ID, Unsigned.getUnsignedShort(byteBuffer));
    }
    
    @Test
    public void testDeserializeInput() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HEADER12);
        byteBuffer.get();
        swtHeader12.deserialize(byteBuffer);
        assertEquals(GdnMessageType.StandardReport, swtHeader12.messageType());
        assertEquals(0x09, swtHeader12.messageType().getNumber().intValue());
        assertEquals(0x0001, swtHeader12.getSessionId().intValue());
    }

    @Test
    public void testSerializeOutput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        SwtHeader12 swtHeader12 = new SwtHeader12(25, GdnMessageType.DemandPoll);
        swtHeader12.serialize(byteBuffer);
        byteBuffer.flip();

        assertEquals(12, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(GdnMessageType.DemandPoll.getNumber().intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(25, Unsigned.getUnsignedShort(byteBuffer));
    }

}
