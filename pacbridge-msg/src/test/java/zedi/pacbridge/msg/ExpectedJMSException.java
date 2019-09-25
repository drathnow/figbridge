package zedi.pacbridge.msg;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.jms.JMSException;

public class ExpectedJMSException  extends JMSException {

    
    public ExpectedJMSException() {
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