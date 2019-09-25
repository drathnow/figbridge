package zedi.pacbridge.gdn;

import static org.junit.Assert.assertTrue;
import static zedi.pacbridge.gdn.messages.GdnMessageType.ExtendedReport;
import static zedi.pacbridge.gdn.messages.GdnMessageType.StandardReport;
import static zedi.pacbridge.gdn.messages.GdnMessageType.WriteIoPoint;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.messages.ExtendedReportMessage;
import zedi.pacbridge.gdn.messages.GdnMessageFactory;
import zedi.pacbridge.gdn.messages.StandardReportMessage;
import zedi.pacbridge.gdn.messages.WriteIoPointControl;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class GdnMessageFactoryTest extends BaseTestCase {
    private static final String WRITE_IO_BYTES = "07 00 02 3F AA D8 E2";
    private static final String EXTENDED_REPORT_BYTES = "01 51 3E 4A 18 01 25 00 0A 08 01 3F 9C 28 F6";
    private static final String STANDARD_REPORT_BYTES = "01 51 3E 4B 63 00";

    @Test
    public void shouldReturnCorrectMessageType() throws Exception {
        GdnMessageFactory messageFactory = new GdnMessageFactory();
        byte[] bytes = HexStringDecoder.hexStringAsBytes(WRITE_IO_BYTES);
        assertTrue(messageFactory.messageFromByteBuffer(WriteIoPoint.getNumber(), ByteBuffer.wrap(bytes)) instanceof WriteIoPointControl);

        bytes = HexStringDecoder.hexStringAsBytes(EXTENDED_REPORT_BYTES);
        assertTrue(messageFactory.messageFromByteBuffer(ExtendedReport.getNumber(), ByteBuffer.wrap(bytes)) instanceof ExtendedReportMessage);

        bytes = HexStringDecoder.hexStringAsBytes(STANDARD_REPORT_BYTES);
        assertTrue(messageFactory.messageFromByteBuffer(StandardReport.getNumber(), ByteBuffer.wrap(bytes)) instanceof StandardReportMessage);
    }
}

