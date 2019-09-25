package zedi.pacbridge.app.events;

public class EventParserException extends Exception {

    public EventParserException(String message) {
        super(message);
    }
    
    public EventParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
