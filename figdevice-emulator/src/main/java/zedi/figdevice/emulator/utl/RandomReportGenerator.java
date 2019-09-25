package zedi.figdevice.emulator.utl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ReadingFlags;
import zedi.pacbridge.zap.reporting.ReportHeader;
import zedi.pacbridge.zap.reporting.ZapReport;
import zedi.pacbridge.zap.values.ZapValue;

public class RandomReportGenerator implements ReportGenerator {
    private static final Integer POLLSET_NUMBER = 1;
    private static final Integer VERSION = 1;
    private static Random random = new Random(System.currentTimeMillis());
    
    private Integer minNumberOfReadings;
    private Integer maxNumberOfReadings;
    private ValueGenerator valueGenerator;
    private ReportIdGenerator reportIdGenerator;
    private ReportTimeGenerator reportTimeGenerator;    
    private EventIdGenerator eventIdGenerator;

    public RandomReportGenerator(Integer minNumberOfReadings, Integer maxNumberOfReadings, ValueGenerator valueGenerator, ReportIdGenerator reportIdGenerator, ReportTimeGenerator reportTimeGenerator) {
        this.minNumberOfReadings = minNumberOfReadings;
        this.maxNumberOfReadings = maxNumberOfReadings;
        this.valueGenerator = valueGenerator;
        this.reportIdGenerator = reportIdGenerator;
        this.reportTimeGenerator = reportTimeGenerator;
        this.eventIdGenerator = new EventIdGenerator();
    }

    @Override
    public ZapReport nextReport() {
        Long timestamp = System.currentTimeMillis();
        int readingCount = numberOfReadings();
        List<IoPointReading> readings = gerneratedReadings(readingCount);
        List<IoPointTemplate> templates = new ArrayList<IoPointTemplate>();
        int ioId = 1;
        for (IoPointReading reading : readings) {
            IoPointTemplate template = new IoPointTemplate((long)ioId++, reading.value().dataType());
            templates.add(template);
        }
        ReportHeader header = reportHeader(templates);
        Date reportDate = new Date(timestamp);
        List<ReadingCollection> collections = new ArrayList<ReadingCollection>();
        collections.add(new ReadingCollection(reportDate, readings));
        return new ZapReport(header, collections, UUID.randomUUID().toString());
    }

    public Integer secondsUntilNextReport() {
        return reportTimeGenerator.secondsUntilNextReport();
    }

    private Integer numberOfReadings() {
        Integer count = Math.abs(random.nextInt()) % maxNumberOfReadings;
        return (count < minNumberOfReadings) ? (minNumberOfReadings + count) : count;
        
    }
    
    private List<IoPointReading> gerneratedReadings(int readingCount) {
        List<IoPointReading> readings = new ArrayList<>();
        for (int i = 0; i < readingCount; i++) {
            ReadingFlags flags = new ReadingFlags(false, false, ZapAlarmStatus.OK);
            ZapValue value = valueGenerator.nextValue();
            IoPointReading reading = new IoPointReading(flags, value);
            readings.add(reading);
        }
        return readings;
    }
    
    protected ReportHeader reportHeader(List<IoPointTemplate> ioPointTemplates) {
        return new ReportHeader(VERSION, 
                                reportIdGenerator.nextReportId(), 
                                new Date(), 
                                1, 
                                ZapReasonCode.Scheduled, 
                                POLLSET_NUMBER, 
                                ioPointTemplates,
                                eventIdGenerator.nextEventId());

    }    
    
}
