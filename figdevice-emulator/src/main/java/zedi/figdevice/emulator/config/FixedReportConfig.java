package zedi.figdevice.emulator.config;

import org.jdom2.Element;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.FixedBundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.FixedIntervalReportTimeGenerator;
import zedi.figdevice.emulator.utl.FixedReportGenerator;
import zedi.figdevice.emulator.utl.RandomIntervalReportTimeGenerator;
import zedi.figdevice.emulator.utl.RandomValueGenerator;
import zedi.figdevice.emulator.utl.ReportGenerator;
import zedi.figdevice.emulator.utl.ReportIdGenerator;
import zedi.figdevice.emulator.utl.ReportTimeGenerator;
import zedi.figdevice.emulator.utl.ValueGenerator;
import zedi.figdevice.emulator.utl.ValueType;

public class FixedReportConfig implements ReportConfig {
    public static final String INTERVAL_SECONDS_TAG = "IntervalSeconds";
    public static final String NUMBER_OF_READINGS_TAG = "NumberOfReadings";
    public static final String INTERVAL_TYPE_ATTR_TAG = "intervalType";
    public static final String POLLSET_TAG = "PollsetNumber";
    
    private Integer intervalSeconds;
    private Integer numberOfReadings;
    private ValueType intervalType;
    
    private FixedReportConfig(Integer intervalSeconds, Integer numberOfReadings, ValueType intervalType) {
        this.intervalSeconds = intervalSeconds;
        this.numberOfReadings = numberOfReadings;
        this.intervalType = intervalType;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }
    
    public Integer getNumberOfReadings() {
        return numberOfReadings;
    }
    
    public ValueType getIntervalType() {
        return intervalType;
    }
    
    public BundledReportMessageGenerator bundledReportMessageGenerator(Integer reportsPerMessage) {
        return new FixedBundledReportMessageGenerator(reportsPerMessage, reportGenerator()); 
    }
    
    public static FixedReportConfig fixedIntervalReportConfigFromElement(Element element) {
        ValueType intervalType = ValueType.valueTypeForName(element.getAttributeValue(INTERVAL_TYPE_ATTR_TAG));
        Integer interval = Integer.parseInt(element.getChildText(INTERVAL_SECONDS_TAG));
        Integer numberOfValues = Integer.parseInt(element.getChildText(NUMBER_OF_READINGS_TAG)); 
        return new FixedReportConfig(interval, numberOfValues, intervalType);
    }
    
    private ReportGenerator reportGenerator() {
        ValueGenerator valueGenerator = new RandomValueGenerator();
        ReportTimeGenerator reportTimeGenerator;
        if (ValueType.FIXED == intervalType)
            reportTimeGenerator = new FixedIntervalReportTimeGenerator(intervalSeconds);
        else
            reportTimeGenerator = new RandomIntervalReportTimeGenerator(intervalSeconds);
        return new FixedReportGenerator(numberOfReadings, valueGenerator, new ReportIdGenerator(), reportTimeGenerator);
    }

}
