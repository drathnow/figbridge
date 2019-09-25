package zedi.figdevice.emulator.utl;

import zedi.pacbridge.zap.reporting.ZapReport;

public interface ReportGenerator {
    public ZapReport nextReport();
    public Integer secondsUntilNextReport();
}
