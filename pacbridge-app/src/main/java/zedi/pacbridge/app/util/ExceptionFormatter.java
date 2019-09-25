package zedi.pacbridge.app.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import javax.ejb.Stateless;

@Stateless
public class ExceptionFormatter implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public String exceptionAsString(Throwable throwable) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(arrayOutputStream);
        logExceptionToPrintStream(throwable, printStream);
        return arrayOutputStream.toString();
    }
    
    private void logExceptionToPrintStream(Throwable throwable, PrintStream printStream) {
        throwable.printStackTrace(printStream);
        if (throwable.getCause() != null) {
            printStream.println("Caused by:");
            logExceptionToPrintStream(throwable.getCause(), printStream);
        }
    }
}
