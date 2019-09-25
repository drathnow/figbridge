package zedi.pacbridge.net.controls;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class ControlStatus extends NamedType implements Serializable {
    private static final long serialVersionUID = 1001;
    
    private static final int UNKOWN_NUMBER = 0;
    private static final int SUCCESS_NUMBER = 1;
    private static final int FAILURE_NUMBER = 2;
    private static final int RUNNING_NUMBER = 3;
    private static final int PENDING_NUMBER = 4;
    private static final int PROCESSING_NUMBER = 5;
    private static final int CANCELLED_NUMBER = 6;
    private static final int CANCEL_PENDING_NUMBER = 7;
    private static final int TIMED_OUT_NUMBER = 8;
    private static final int ACKNOWLEDGED_NUMBER = 9;

    public static final ControlStatus UNKOWN = new ControlStatus("Unknown", UNKOWN_NUMBER);
    public static final ControlStatus SUCCESS = new ControlStatus("Success", SUCCESS_NUMBER);
    public static final ControlStatus FAILURE = new ControlStatus("Failure", FAILURE_NUMBER);
    public static final ControlStatus RUNNING = new ControlStatus("Running", RUNNING_NUMBER);
    public static final ControlStatus PENDING = new ControlStatus("Pending", PENDING_NUMBER);
    public static final ControlStatus PROCESSING = new ControlStatus("Processing", PROCESSING_NUMBER);
    public static final ControlStatus CANCELLED = new ControlStatus("Cancelled", CANCELLED_NUMBER);
    public static final ControlStatus CANCEL_PENDING = new ControlStatus("Cancel Pending", CANCEL_PENDING_NUMBER);
    public static final ControlStatus TIMED_OUT = new ControlStatus("Timed out", TIMED_OUT_NUMBER);
    public static final ControlStatus ACKNOWLEDGED = new ControlStatus("Acknowledged", ACKNOWLEDGED_NUMBER);

    private ControlStatus(String name, Integer number) {
        super(name, number);
    }

    public static ControlStatus controlStatusForNumber(Integer number) {
        switch (number) {
            case UNKOWN_NUMBER :
                return UNKOWN;
            case SUCCESS_NUMBER :
                return SUCCESS;
            case FAILURE_NUMBER :
                return FAILURE;
            case RUNNING_NUMBER :
                return RUNNING;
            case PENDING_NUMBER :
                return PENDING;
            case PROCESSING_NUMBER :
                return PROCESSING;
            case CANCELLED_NUMBER :
                return CANCELLED;
            case CANCEL_PENDING_NUMBER :
                return CANCEL_PENDING;
            case TIMED_OUT_NUMBER :
                return TIMED_OUT;
            case ACKNOWLEDGED_NUMBER :
                return ACKNOWLEDGED;
        }
        throw new IllegalArgumentException("Unknown ControlStatus number : '" + number + "'");
    }

    public static ControlStatus eventStatusForName(String keyName) {
        if (UNKOWN.getName().equalsIgnoreCase(keyName))
            return UNKOWN;
        if (SUCCESS.getName().equalsIgnoreCase(keyName))
            return SUCCESS;
        if (FAILURE.getName().equalsIgnoreCase(keyName))
            return FAILURE;
        if (RUNNING.getName().equalsIgnoreCase(keyName))
            return RUNNING;
        if (PENDING.getName().equalsIgnoreCase(keyName))
            return PENDING;
        if (PROCESSING.getName().equalsIgnoreCase(keyName))
            return PROCESSING;
        if (CANCELLED.getName().equalsIgnoreCase(keyName))
            return CANCELLED;
        if (CANCEL_PENDING.getName().equalsIgnoreCase(keyName))
            return CANCEL_PENDING;
        if (TIMED_OUT.getName().equalsIgnoreCase(keyName))
            return TIMED_OUT;
        if (ACKNOWLEDGED.getName().equalsIgnoreCase(keyName))
            return ACKNOWLEDGED;
        throw new IllegalArgumentException("Unknown ControlStatus name: '" + keyName + "'");
    }

}
