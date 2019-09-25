package zedi.pacbridge.app.messaging;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ReadingFlags;
import zedi.pacbridge.zap.reporting.ReportHeader;
import zedi.pacbridge.zap.reporting.ZapReport;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapFloat;

public class ReportSizeCalculationTest {
    private static final Integer REPORTID1 = 1;
    
    private ZapReport reportWithFloats(int count) {
        ReadingFlags flags = new ReadingFlags(ZapAlarmStatus.OK);
        List<IoPointReading> readings = new ArrayList<>();
        for (int i = 0; i < count; i++)
            readings.add(new IoPointReading(flags, new ZapFloat((float)i)));
        ReadingCollection collection = new ReadingCollection(new  Date(), readings);
        List<ReadingCollection> collections = new ArrayList<>();
        collections.add(collection);
        List<IoPointTemplate> templates = new ArrayList<>();
        for (int i = 0; i < count; i++)
            templates.add(new IoPointTemplate((long)i, ZapDataType.Float));
        ReportHeader header = new ReportHeader(REPORTID1, new Date(), 5, ZapReasonCode.Scheduled, 1, templates, 0L);
        return new ZapReport(header, collections, "foomanchoo");
    }
    
    private void outputSizeOfReportWithNFloats(int n) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1028);
        ZapReport report = reportWithFloats(n);
        report.serialize(byteBuffer);
        System.out.println("Size of ZapReport with " + n + " floats: " + byteBuffer.position());
        Set<Integer> reportIds = new HashSet<>();
        reportIds.add(REPORTID1);
        Map<Integer, ZapReport> reportMap = new TreeMap<>();
        reportMap.put(REPORTID1, report);
        BundledReportMessage message = new BundledReportMessage(1, reportIds, reportMap);
        byteBuffer.clear();
        message.serialize(byteBuffer);
        System.out.println("Size of Bundled Report Message with " + n + " floats: " + byteBuffer.position());
    }
    
    @Test
    public void shouldCalculateSizeOfSiteReportWith5Floats() throws Exception {
        outputSizeOfReportWithNFloats(5);
        outputSizeOfReportWithNFloats(20);
    }

}
