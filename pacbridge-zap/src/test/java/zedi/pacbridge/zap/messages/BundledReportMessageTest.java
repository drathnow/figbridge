package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.zap.reporting.ZapReport;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BundledReportMessage.class, ZapReport.class})
public class BundledReportMessageTest extends BaseTestCase {
    private static final String RPT = "|01|02|53|00|01|00|00|61|30|01|00|00|00|00|00|00|00|00|00|00|61|30|54|E5|0C|4A|00|02|00|01|00|00|0A|00|00|00|6F|08|00|00|00|6E|01|54|E5|0C|4A|00|3F|80|00|00|00|01|";
                                      //01 02 53 00 01 00 00 61 30 01 00 00 00 00 00 00 00 00 00 00 61 30             00 02 00 01 00 00 0A 00 00 00 6F 08 00 00 00 6E 01 54 E5 0C 4A 40 3F 80 00 00 40 01
    private static final String RPT2 = "01 00 00 00 02 00 00 00 10 00 00 00 11 01 00 00 00 00 00 00 00 00 00 00 00 7b 55 12 0b f7 00 01 00 01 00 00 00 00 00 00 01 0a 55 12 0b f7 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 7b 55 12 0b f7 00 01 00 01 00 00 00 00 00 00 01 0a 55 12 0b f7 00 00 00";
    
    @Test
    public void shouldDeserializeAndSerialize() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(RPT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        BundledReportMessage reportMessage = BundledReportMessage.bundledReportMessageFromByteBuffer(byteBuffer);
        assertNotNull(reportMessage);
        assertEquals(1, reportMessage.reportIds().size());
        
        ByteBuffer outputBuffer = ByteBuffer.allocate(1024);
        reportMessage.serialize(outputBuffer);
        outputBuffer.flip();    
        reportMessage = BundledReportMessage.bundledReportMessageFromByteBuffer(outputBuffer);
        assertNotNull(reportMessage);
        assertEquals(1, reportMessage.reportIds().size());
        
    }

    @Test
    public void shouldDeserializeRpt2() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(RPT2);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        BundledReportMessage reportMessage = BundledReportMessage.bundledReportMessageFromByteBuffer(byteBuffer);

        assertNotNull(reportMessage);
        assertEquals(2, reportMessage.reportIds().size());
    }    
    
    @Test
    public void shouldDeserializeRpt() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(RPT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        BundledReportMessage reportMessage = BundledReportMessage.bundledReportMessageFromByteBuffer(byteBuffer);

        assertNotNull(reportMessage);
        assertEquals(1, reportMessage.reportIds().size());
    }    
}