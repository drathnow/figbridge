package zedi.pacbridge.app.events;

public class InvalidEventFormatException extends Exception {

    public InvalidEventFormatException(String message) {
        super(message);
    }

    public InvalidEventFormatException(String message, Exception e) {
        super(message, e);
    }
}
