package zedi.figdevice.emulator.utl;

import zedi.pacbridge.zap.messages.BundledReportMessage;

public interface BundledReportMessageGenerator {
    public BundledReportMessage nextBundledReportMessage(Integer sequenceNumber);
    public Integer secondsUntilNextReportIsDue();
    public boolean isFinished();
}
