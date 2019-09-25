package zedi.pacbridge.gdn.messages;

import java.text.MessageFormat;

import zedi.pacbridge.gdn.IoPointIndexes;
import zedi.pacbridge.net.controls.ControlStatus;

public abstract class GdnResponseStrategy  {

    public static final String ERROR_MSG_UNKNOWN_ERROR_CODE = " - Unknown error code";
    public static final String EROR_MSG_INVALID_INDEX_OR_POLLSET_NUMBER = "2 - Invalid index or pollset number";
    
    public static final String ERROR_STATUS_FMT = "Error status returned from PAC: {0}";

    private ControlStatus finalStatus;
    private String finalStatusMessage;
    
    public ControlStatus finalStatus() {
        return finalStatus;
    }

    public boolean isFinished() {
        return finalStatus != null;
    }

    public String finalStatusMessage() {
        return finalStatusMessage;
    }
    
    public void forceFinished(ControlStatus eventStatus, String statusMessage) {
        this.finalStatus = eventStatus;
        this.finalStatusMessage= statusMessage;
    }
    
    protected void updateStatus(ControlStatus status) {
        updateStatus(status, null);
    }
    
    protected void updateStatus(ControlStatus status, String message) {
        finalStatus = status;
        finalStatusMessage = message;
    }

    protected void checkAlarmStatusAndFinish(IoPointReportItem item) {
        if (item.getAlarmStatus() != null && item.getAlarmStatus().isDataUnavailable()) {
            String message = MessageFormat.format(ERROR_STATUS_FMT, new Object[]{item.getAlarmStatus().getName()});
            updateStatus(ControlStatus.FAILURE, message);
        } else
            updateStatus(ControlStatus.SUCCESS);
    }
        
    protected void checkForDiagnosticErrorCode(IoPointReportMessage<?> reportMessage) {
        IoPointReportItem reportItem = reportMessage.reportItemWithIndex(IoPointIndexes.INDEX_FOR_ERROR_CODE);
        if (reportItem != null) {
            Integer errorCode = (Integer)reportItem.getValue().getValue();
            String text = errorTextForPACErrorCode(errorCode);
            String message = MessageFormat.format(GdnResponseStrategy.ERROR_STATUS_FMT, new Object[]{text});
            updateStatus(ControlStatus.FAILURE, message);
        }
    }
    
    protected String errorTextForPACErrorCode(int errorCode) {
        switch (errorCode) {
            case 2 :
                return EROR_MSG_INVALID_INDEX_OR_POLLSET_NUMBER;
            default :
                return errorCode + ERROR_MSG_UNKNOWN_ERROR_CODE;
        }
    }
    
    protected boolean isReportMessageType(GdnMessageType messageType) {
        return messageType == GdnMessageType.StandardReport || messageType == GdnMessageType.ExtendedReport;
    }
    
    protected boolean isStandardReportMessage(IoPointReportMessage<?> reportMessage) {
        return reportMessage.messageType() == GdnMessageType.StandardReport;
    }
    
    
}
