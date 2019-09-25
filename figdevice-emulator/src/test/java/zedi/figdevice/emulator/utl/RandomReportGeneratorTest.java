package zedi.figdevice.emulator.utl;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;
import zedi.pacbridge.zap.reporting.ZapReport;

public class RandomReportGeneratorTest extends BaseTestCase {

    @Test
    @Ignore
    public void shouldTestReportGenerator() throws Exception {
        ValueGenerator valueGenerator = new RandomValueGenerator();
        ReportIdGenerator reportIdGenerator = new ReportIdGenerator();
        ReportTimeGenerator reportTimeGenerator = new FixedIntervalReportTimeGenerator(10);
        RandomReportGenerator reportGenerator = new RandomReportGenerator(10, 20, valueGenerator, reportIdGenerator, reportTimeGenerator);
        ZapMessageDecoder messageDecoder = new ZapMessageDecoder();
        
        for (int i = 0; i < 10; i++) {
            ZapReport report = reportGenerator.nextReport();
            System.out.println(messageDecoder.decodeMessage(report));
            System.out.println();
        }
    }
}
