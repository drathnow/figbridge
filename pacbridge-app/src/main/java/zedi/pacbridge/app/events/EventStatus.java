package zedi.pacbridge.app.events;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class EventStatus extends NamedType implements Serializable {
    private static final long serialVersionUID = 1001;

    //
    // With the exception of UNKNOWN_NUMBER, these numbers map directly to
    // values in teh Zedi Access DB.
    // They CANNOT be changed!
    //
    private static final int UNKOWN_NUMBER = -1;
    private static final int SUCCESS_NUMBER = 0;
    private static final int FAILURE_NUMBER = 1;
    private static final int RUNNING_NUMBER = 2;
    private static final int PENDING_NUMBER = 3;
    private static final int PROCESSING_NUMBER = 4;
    private static final int CANCELLED_NUMBER = 5;
    private static final int CANCEL_PENDING_NUMBER = 6;

    public static final EventStatus Unknown = new EventStatus("Unknown", UNKOWN_NUMBER);
    public static final EventStatus Success = new EventStatus("Success", SUCCESS_NUMBER);
    public static final EventStatus Failure = new EventStatus("Failed", FAILURE_NUMBER);
    public static final EventStatus Running = new EventStatus("Running", RUNNING_NUMBER);
    public static final EventStatus Pending = new EventStatus("Pending", PENDING_NUMBER);
    public static final EventStatus Processing = new EventStatus("Processing", PROCESSING_NUMBER);
    public static final EventStatus Cancelled = new EventStatus("Cancelled", CANCELLED_NUMBER);
    public static final EventStatus CancelPending = new EventStatus("Cancel Pending", CANCEL_PENDING_NUMBER);

    private EventStatus(String name, Integer number) {
        super(name, number);
    }

    public static EventStatus unknownEventWithMessage(String message) {
        return new EventStatus("Uknown status: " + message, -1);
    }

    public static EventStatus eventStatusForNumber(Integer number) {
        switch (number) {
            case UNKOWN_NUMBER :
                return Unknown;
            case SUCCESS_NUMBER :
                return Success;
            case FAILURE_NUMBER :
                return Failure;
            case RUNNING_NUMBER :
                return Running;
            case PENDING_NUMBER :
                return Pending;
            case PROCESSING_NUMBER :
                return Processing;
            case CANCELLED_NUMBER :
                return Cancelled;
            case CANCEL_PENDING_NUMBER :
                return CancelPending;
        }
        throw new IllegalArgumentException("Unknown EventStatus number : '" + number + "'");
    }

    public static EventStatus eventStatusForName(String keyName) {
        if (Unknown.getName().equalsIgnoreCase(keyName))
            return Unknown;
        if (Success.getName().equalsIgnoreCase(keyName))
            return Success;
        if (Failure.getName().equalsIgnoreCase(keyName))
            return Failure;
        if (Running.getName().equalsIgnoreCase(keyName))
            return Running;
        if (Pending.getName().equalsIgnoreCase(keyName))
            return Pending;
        if (Processing.getName().equalsIgnoreCase(keyName))
            return Processing;
        if (Cancelled.getName().equalsIgnoreCase(keyName))
            return Cancelled;
        if (CancelPending.getName().equalsIgnoreCase(keyName))
            return CancelPending;
        return null;
    }

}
