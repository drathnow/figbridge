package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.utl.HexStringDecoder;


public class StandardReportMessageTest extends StandardReportItemTest {

    protected static final String LEHEXREPORT = "01 83 37 fa 3e 17 01 00 08 c0 5e e1 41 02 00 08 98 a3 e4 45 03 00 08 a6 49 14 41 04 00 08 3f d4 e2 42 05 00 08 90 56 58 3f 06 00 08 00 00 00 00 07 00 08 08 88 2b 3e 08 00 08 00 00 00 00 09 00 08 00 00 00 00 0a 00 08 00 00 00 00 0b 00 08 00 00 00 00 0c 00 08 bc 02 96 c2 12 00 08 47 ea 17 42 15 00 01 00 16 00 01 00 1b 00 01 01 23 00 01 00 29 00 01 01 2d 00 01 00 31 00 01 00 34 00 01 00 35 00 01 00 3d 00 01 01";
    private static final Date timestamp = new Date();
    protected static final int TEST_PID = 1;
    protected static final int TEST_TIMESTAMP = (int)System.currentTimeMillis() / 1000;
    protected static final int TEST_ITEMCOUNT = 2;

    protected static final int HEXREPORT_PID = 1;
    protected static final int HEXREPORT_ITEMCOUNT = 23;
    protected long HEXREPORT_TIMESTAMP;
            

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GregorianCalendar fooGc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        fooGc.set(2003, 5, 26, 0, 0, 3);
        HEXREPORT_TIMESTAMP = fooGc.getTime().getTime() / 1000;
    }

    @Test
    public void testParseHexPacket() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(LEHEXREPORT)).order(ByteOrder.LITTLE_ENDIAN);
        StandardReportMessage pointReport = StandardReportMessage.standardReportMessageFromByteBuffer(byteBuffer);

        assertEquals(HEXREPORT_PID, pointReport.getPollSetNumber());
        assertEquals(HEXREPORT_TIMESTAMP, pointReport.getTimeStamp().getTime() / 1000);
        assertEquals(HEXREPORT_ITEMCOUNT, pointReport.getReportItems().size());
        Iterator<StandardReportItem> iter = pointReport.getReportItems().iterator();
        assertReportItemEquals(iter.next(), 8, 1, new Float(28.1713));
        assertReportItemEquals(iter.next(), 8, 2, new Float(7316.45));
        assertReportItemEquals(iter.next(), 8, 3, new Float(9.26798));
        assertReportItemEquals(iter.next(), 8, 4, new Float(113.415));
        assertReportItemEquals(iter.next(), 8, 5, new Float(0.845071));
        assertReportItemEquals(iter.next(), 8, 6, new Float(0));
        assertReportItemEquals(iter.next(), 8, 7, new Float(0.167511));
        assertReportItemEquals(iter.next(), 8, 8, new Float(0));
        assertReportItemEquals(iter.next(), 8, 9, new Float(0));
        assertReportItemEquals(iter.next(), 8, 10, new Float(0));
        assertReportItemEquals(iter.next(), 8, 11, new Float(0));
        assertReportItemEquals(iter.next(), 8, 12, new Float(-75.0053));
        assertReportItemEquals(iter.next(), 8, 18, new Float(37.9788));
        assertReportItemEquals(iter.next(), 1, 21, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 22, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 27, new Integer(1));
        assertReportItemEquals(iter.next(), 1, 35, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 41, new Integer(1));
        assertReportItemEquals(iter.next(), 1, 45, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 49, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 52, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 53, new Integer(0));
        assertReportItemEquals(iter.next(), 1, 61, new Integer(1));
    }

    @Test
    public void testSerializeInput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        StandardReportItem item1 = mock(StandardReportItem.class);
        StandardReportItem item2 = mock(StandardReportItem.class);
        
        List<StandardReportItem> items = Arrays.asList(item1, item2);
        
        StandardReportMessage standardReportMessage = new StandardReportMessage(items, TEST_PID, timestamp);
        standardReportMessage.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(TEST_PID, byteBuffer.get());
        assertEquals(timestamp.getTime()/1000, byteBuffer.getInt());
        assertEquals(2, byteBuffer.get());
        verify(item1).serialize(eq(byteBuffer));
        verify(item2).serialize(eq(byteBuffer));
    }

    protected void assertReportItemEquals(StandardReportItem item, int type, int index, Number value) {
        assertEquals(item.getValue().dataType().getNumber().intValue(), type);
        assertEquals(item.getIndex(), index);
        assertEquals(((Number)item.getValue().getValue()).doubleValue(), value.doubleValue(), .01);
    }
}
