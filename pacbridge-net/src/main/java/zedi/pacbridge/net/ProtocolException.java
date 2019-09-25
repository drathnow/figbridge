package zedi.pacbridge.net;

public class ProtocolException extends Exception {

    public ProtocolException() {
    }
    
    public ProtocolException(String message) {
        super(message);
    }
    
    public ProtocolException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
