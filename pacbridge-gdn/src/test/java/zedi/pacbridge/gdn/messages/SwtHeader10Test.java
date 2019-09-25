package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;


public class SwtHeader10Test extends BaseTestCase {
    protected static final String LEHEXHEADER10 = "0a 09";
    protected static final String LEHEXBROKENHEADER10 = "0a";
    protected static final int TEST_MSG_TYPE = 9;
    private SwtHeader10 swtHeader10;

    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        swtHeader10 = new SwtHeader10();
    }

    @After
    public void tearDown() throws Exception {
        swtHeader10 = null;
        super.tearDown();
    }

    @Test
    public void testSerializeInput() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(LEHEXHEADER10)).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.get();
        swtHeader10.deserialize(byteBuffer);
        assertEquals(TEST_MSG_TYPE, swtHeader10.messageType().getNumber().intValue());
    }

}
