package zedi.pacbridge.test;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;

public class ExpectedConnectException extends ConnectException {
    
    public ExpectedConnectException() {
        super("This is an expected test exception");
    }
    
    @Override
    public void printStackTrace() {
        printStackTrace(System.out);
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        s.println("This is an expected exception");
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        s.println("This is an expected exception");
    }    
}
