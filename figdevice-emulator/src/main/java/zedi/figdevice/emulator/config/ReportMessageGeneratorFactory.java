package zedi.figdevice.emulator.config;

import org.jdom2.Element;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;

public class ReportMessageGeneratorFactory {
    public static final String FIXED_REPORT_TAG = "FixedReport";
    public static final String RANDOM_REPORT_TAG = "RandomReport";

    static BundledReportMessageGenerator reportGeneratorForElement(Element element) {
        if (element.getChild(FIXED_REPORT_TAG) != null) {
            FixedReportConfig config = FixedReportConfig.fixedIntervalReportConfigFromElement(element.getChild(FIXED_REPORT_TAG));
            return config.bundledReportMessageGenerator(1);
        }
        if (element.getChild(RANDOM_REPORT_TAG) != null) {
            RandomReportConfig config = RandomReportConfig.randomIntervalReportConfigFromElement(element.getChild(RANDOM_REPORT_TAG));
            return config.bundledReportMessageGenerator(1);
        }
        throw new UnsupportedOperationException("Unknown report generator type: " + element.getName());
    }
}
