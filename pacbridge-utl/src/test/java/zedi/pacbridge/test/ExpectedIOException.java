package zedi.pacbridge.test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ExpectedIOException extends IOException {

    
    public ExpectedIOException() {
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
