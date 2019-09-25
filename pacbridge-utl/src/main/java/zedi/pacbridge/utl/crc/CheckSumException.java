package zedi.pacbridge.utl.crc;

import java.io.IOException;

public class CheckSumException extends IOException {

    public CheckSumException() {
    }
    
    public CheckSumException(String message) {
        super(message);
    }
}
