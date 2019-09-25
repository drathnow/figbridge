package zedi.figbridge.monitor;

import java.io.IOException;

import zedi.figbridge.monitor.utl.BundledReportMessageGenerator;
import zedi.figbridge.monitor.utl.ClientAuthenticator;
import zedi.figbridge.monitor.utl.ReportMessageListener;
import zedi.figbridge.monitor.utl.ReportSender;

public class BridgeMonitor {
    
    private ClientAuthenticator authenticator;
    private ReportSender reportSender;
    private ReportMessageListener listener;
    private BundledReportMessageGenerator messageGenerator;
    private Integer mqWaitTimeSeconds;

    
    public BridgeMonitor(ClientAuthenticator authenticator, ReportSender reportSender, ReportMessageListener listener, BundledReportMessageGenerator messageGenerator, Integer mqWaitTimeSeconds) {
        this.authenticator = authenticator;
        this.reportSender = reportSender;
        this.listener = listener;
        this.messageGenerator = messageGenerator;
        this.mqWaitTimeSeconds = mqWaitTimeSeconds;
    }

    /**
     * Status codes:
     * 
     *   0 - Success
     *   1 - Unable to connect to bridge
     *   2 - Unable to authenticate
     *   3 - Error sending report to bridge (Text indicates error)
     *   4 - No message published by bridge
     *   
     * @param args
     */
    public Status statusOfBridge() {
        Status finalStatus = Status.SUCCESS;

        try {
            if (finalStatus == Status.SUCCESS) {
                if (authenticator.didAuthenticate()) {
                    if (reportSender.didSendReportWithMessageGenerator(messageGenerator)) {
                        Long now = System.currentTimeMillis();
                        Long waitUntil = now + (mqWaitTimeSeconds*1000L);
                        while (listener.isDone() == false && (System.currentTimeMillis() < waitUntil))
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        finalStatus = listener.isDone() ? Status.SUCCESS : Status.NO_MSG;
                    } else
                        finalStatus = Status.sendErrorWithMessage(reportSender.getLastErrorText());
                } else
                    finalStatus = Status.AUTH_FAILURE;
            }
        } catch (IOException e) {
            finalStatus = Status.sendErrorWithMessage(e.toString());
        }
        return finalStatus;
    }
}
