package zedi.figdevice.emulator.config;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;

public interface ReportConfig {
    public BundledReportMessageGenerator bundledReportMessageGenerator(Integer reportsPerMessage);
}
