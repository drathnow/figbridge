package zedi.swingutl.beans;


import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class TextPaneAppender extends AppenderSkeleton {

    protected ConsoleTextPane consoleTextPane;

    public TextPaneAppender(ConsoleTextPane consoleTextPane, Layout layout) {
        setLayout(layout);
        this.consoleTextPane = consoleTextPane;
    }

    @Override
    protected synchronized void append(LoggingEvent loggingEvent) {
        String exceptionInformation[] = null;
        String loggingString = getLayout().format(loggingEvent);
        if (loggingEvent.getThrowableInformation() != null)
            exceptionInformation = loggingEvent.getThrowableInformation().getThrowableStrRep();
        switch (loggingEvent.getLevel().toInt()) {
            case Level.TRACE_INT :
                consoleTextPane.addTraceOutput(loggingString);
                if (exceptionInformation != null)
                    for (int i = 0; i < exceptionInformation.length; i++) {
                        consoleTextPane.addTraceOutput(exceptionInformation[i] + '\n');
                    }
                break;

            case Level.DEBUG_INT :
                consoleTextPane.addDebugOutput(loggingString);
                if (exceptionInformation != null)
                    for (int i = 0; i < exceptionInformation.length; i++) {
                        consoleTextPane.addDebugOutput(exceptionInformation[i] + '\n');
                    }
                break;

            case Level.WARN_INT :
                consoleTextPane.addWarnOutput(loggingString);
                if (exceptionInformation != null)
                    for (int i = 0; i < exceptionInformation.length; i++) {
                        consoleTextPane.addWarnOutput(exceptionInformation[i] + '\n');
                    }
                break;
            case Level.ERROR_INT :
            case Level.FATAL_INT :
                consoleTextPane.addErrorOutput(loggingString);
                if (exceptionInformation != null)
                    for (int i = 0; i < exceptionInformation.length; i++) {
                        consoleTextPane.addErrorOutput(exceptionInformation[i] + '\n');
                    }
                break;
            default :
                consoleTextPane.addInfoOutput(loggingString);
                if (exceptionInformation != null)
                    for (int i = 0; i < exceptionInformation.length; i++) {
                        consoleTextPane.addInfoOutput(exceptionInformation[i] + '\n');
                    }
                break;
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void close() {
    }
}