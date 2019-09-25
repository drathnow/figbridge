package zedi.pacbridge.utl.crc;


public class CrcException extends Exception {
    
    public CrcException() {
    }
    
    public CrcException(String message) {
        super(message);
    }
    
    public CrcException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
