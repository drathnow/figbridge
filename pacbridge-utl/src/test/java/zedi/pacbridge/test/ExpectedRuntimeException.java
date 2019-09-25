package zedi.pacbridge.test;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExpectedRuntimeException extends RuntimeException {

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