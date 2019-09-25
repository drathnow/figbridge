package zedi.figdevice.emulator.utl;

import zedi.pacbridge.zap.messages.BundledReportMessage;

public class SingleFixedBundledReportMessageGenerator extends FixedBundledReportMessageGenerator {
    private boolean reportGenerated;
    
    public SingleFixedBundledReportMessageGenerator(Integer numberOfReportsPerMessage, ReportGenerator reportGenerator) {
        super(numberOfReportsPerMessage, reportGenerator);
    }

    @Override
    public BundledReportMessage nextBundledReportMessage(Integer sequenceNumber) {
        reportGenerated = true;
        return super.nextBundledReportMessage(sequenceNumber);
    }
    
    @Override
    public boolean isFinished() {
        return reportGenerated;
    }
}
