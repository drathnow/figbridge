package zedi.figdevice.emulator.config;

import org.jdom2.Element;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.FixedIntervalReportTimeGenerator;
import zedi.figdevice.emulator.utl.RandomBundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.RandomIntervalReportTimeGenerator;
import zedi.figdevice.emulator.utl.RandomReportGenerator;
import zedi.figdevice.emulator.utl.RandomValueGenerator;
import zedi.figdevice.emulator.utl.ReportGenerator;
import zedi.figdevice.emulator.utl.ReportIdGenerator;
import zedi.figdevice.emulator.utl.ReportTimeGenerator;
import zedi.figdevice.emulator.utl.ValueGenerator;
import zedi.figdevice.emulator.utl.ValueType;

public class RandomReportConfig implements ReportConfig {
    public static final String INTERVAL_SECONDS_TAG = "IntervalSeconds";
    public static final String MIN_NUMBER_OF_READINGS_TAG = "MinNumberOfReadings";
    public static final String MAX_NUMBER_OF_READINGS_TAG = "MaxNumberOfReadings";
    public static final String INTERVAL_TYPE_ATTR_TAG = "intervalType";
    public static final String POLLSET_TAG = "PollsetNumber";
    
    private Integer intervalSeconds;
    private Integer minNumberOfReadings;
    private Integer maxNumberOfReadings;
    private ValueType intervalType;
    
    private RandomReportConfig(Integer intervalSeconds, Integer minNumberOfReadings, Integer maxNumberOfReadings, ValueType intervalType) {
        this.intervalSeconds = intervalSeconds;
        this.minNumberOfReadings = minNumberOfReadings;
        this.maxNumberOfReadings = maxNumberOfReadings;
        this.intervalType = intervalType;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }
    
    public Integer getMinNumberOfReadings() {
        return minNumberOfReadings;
    }
    
    public Integer getMaxNumberOfReadings() {
        return maxNumberOfReadings;
    }
    
    public ValueType getIntervalType() {
        return intervalType;
    }
    
    public BundledReportMessageGenerator bundledReportMessageGenerator(Integer reportsPerMessage) {
        return new RandomBundledReportMessageGenerator(reportsPerMessage, reportGenerator()); 
    }

    public static RandomReportConfig randomIntervalReportConfigFromElement(Element element) {
        ValueType intervalType = ValueType.valueTypeForName(element.getAttributeValue(INTERVAL_TYPE_ATTR_TAG));
        Integer interval = Integer.parseInt(element.getChildText(INTERVAL_SECONDS_TAG));
        Integer minNumberOfValues = Integer.parseInt(element.getChildText(MIN_NUMBER_OF_READINGS_TAG)); 
        Integer maxNumberOfValues = Integer.parseInt(element.getChildText(MAX_NUMBER_OF_READINGS_TAG)); 
        if (minNumberOfValues >= maxNumberOfValues)
            throw new IllegalArgumentException("Value for <MinNumberOfValues> must be less than <MaxNumberOfValues>.");
        return new RandomReportConfig(interval, minNumberOfValues, maxNumberOfValues, intervalType);
    }

    private ReportGenerator reportGenerator() {
        ValueGenerator valueGenerator = new RandomValueGenerator();
        ReportTimeGenerator reportTimeGenerator;
        if (ValueType.FIXED == intervalType)
            reportTimeGenerator = new FixedIntervalReportTimeGenerator(intervalSeconds);
        else
            reportTimeGenerator = new RandomIntervalReportTimeGenerator(intervalSeconds);
        return new RandomReportGenerator(minNumberOfReadings, maxNumberOfReadings, valueGenerator, new ReportIdGenerator(), reportTimeGenerator);
    }
    
}
