package zedi.figbridge.slapper.config;

import org.jdom2.Element;

import zedi.figdevice.emulator.config.FixedReportConfig;
import zedi.figdevice.emulator.config.RandomReportConfig;
import zedi.figdevice.emulator.config.ReportConfig;
import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;

public class FigDeviceConfig {
    public static final String COUNT_ATTRIBUTE_TAG = "count";
    public static final String START_DELAY_ATTRIBUTE_TAG = "startDelaySeconds"; 
    public static final String RECONNECT_ATTRIBUTE_TAG = "reconnectMinutes"; 
    public static final String FIXED_REPORT_TAG = "FixedReport";
    public static final String RANDOM_REPORT_TAG = "RandomReport";
    
    private Integer deviceCount;
    private Integer startDelaySeconds;
    private Integer reconnectSeconds;
    private ReportConfig reportConfig;
    
    public FigDeviceConfig(Integer deviceCount, ReportConfig reportConfig, Integer startDelaySeconds, Integer reconnectSeconds) {
        this.deviceCount = deviceCount;
        this.reportConfig = reportConfig;
        this.startDelaySeconds = startDelaySeconds;
        this.reconnectSeconds = reconnectSeconds;
    }

    public BundledReportMessageGenerator newMessageGenerator(Integer reportsPerMessage) {
        return reportConfig.bundledReportMessageGenerator(reportsPerMessage);
    }
    
    public ReportConfig getReportConfig() {
        return reportConfig;
    }
    
    public Integer getDeviceCount() {
        return deviceCount;
    }
    
    public Integer getStartDelaySeconds() {
        return startDelaySeconds;
    }
    
    public Integer getReconnectSeconds() {
        return reconnectSeconds;
    }
    
    public String getDisplayName() {
        return reportConfig.getClass().getSimpleName();
    }
    
    public static FigDeviceConfig figDeviceConfigForElement(Element element) {
        Integer startDelaySeconds = 0;
        Integer reconnectSeconds = 0;
        String value;
        if ((value = element.getAttributeValue(START_DELAY_ATTRIBUTE_TAG)) != null)
            startDelaySeconds = Integer.parseInt(value);
        if ((value = element.getAttributeValue(RECONNECT_ATTRIBUTE_TAG)) != null)
            reconnectSeconds = Integer.parseInt(value) * 60;
        Integer count = Integer.parseInt(element.getAttributeValue(COUNT_ATTRIBUTE_TAG));
        ReportConfig reportConfig = null;
        if (element.getChild(FIXED_REPORT_TAG) != null)
            reportConfig = FixedReportConfig.fixedIntervalReportConfigFromElement(element.getChild(FIXED_REPORT_TAG));
        if (element.getChild(RANDOM_REPORT_TAG) != null)
            reportConfig = RandomReportConfig.randomIntervalReportConfigFromElement(element.getChild(RANDOM_REPORT_TAG));
        if (reportConfig == null)
            throw new IllegalArgumentException(element.getName() + " element does not contain recognized report generator configuration element");
        return new FigDeviceConfig(count, reportConfig, startDelaySeconds, reconnectSeconds);
    }
}
