package zedi.pacbridge.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jTestHelper extends AppenderSkeleton {

    public List<String> infoMessages;
    public List<String> debugMessages;
    public List<String> warnMessages;
    public List<String> errorMessages;
    public List<String> traceMessages;

    private Level minLevel = Level.INFO;
    protected Class<?> loggingClass;
    protected PatternLayout patternLayout;

    public Log4jTestHelper(Class<?> loggingClass) {
        this.loggingClass = loggingClass;
        this.infoMessages = new ArrayList<String>();
        this.debugMessages = new ArrayList<String>();
        this.warnMessages = new ArrayList<String>();
        this.errorMessages = new ArrayList<String>();
        this.traceMessages = new ArrayList<String>();
    }

    public void setMinLevel(Level aMinLevel) {
        minLevel = aMinLevel;
        Logger.getLogger(loggingClass).setLevel(aMinLevel);
    }

    public void setUp() {
        Logger.getLogger(loggingClass).addAppender(this);
        patternLayout = new PatternLayout("%m");
    }

    public void tearDown() {
        Logger.getLogger(loggingClass).removeAppender(this);
        patternLayout = null;
        clearMessages();
    }

    public void clearMessages() {
        synchronized (infoMessages) {
            infoMessages.clear();
            debugMessages.clear();
            warnMessages.clear();
            errorMessages.clear();
        }
    }

    protected void append(LoggingEvent loggingEvent) {
        if (loggingEvent.getLevel().isGreaterOrEqual(minLevel))
            synchronized (infoMessages) {
                if (Level.INFO.equals(loggingEvent.getLevel()))
                    infoMessages.add(patternLayout.format(loggingEvent));
                if (Level.DEBUG.equals(loggingEvent.getLevel()))
                    debugMessages.add(patternLayout.format(loggingEvent));
                if (Level.WARN.equals(loggingEvent.getLevel()))
                    warnMessages.add(patternLayout.format(loggingEvent));
                if (Level.ERROR.equals(loggingEvent.getLevel()))
                    errorMessages.add(patternLayout.format(loggingEvent));
                if (Level.TRACE.equals(loggingEvent.getLevel()))
                    traceMessages.add(patternLayout.format(loggingEvent));
            }
    }

    public boolean requiresLayout() {
        return false;
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public void close() {
    }

    public boolean wasMessageLogged(String message) {
        synchronized (infoMessages) {
            return infoMessages.contains(message);
        }
    }

    public boolean wasMessageWithSubstringLogged(String substring) {
        synchronized (infoMessages) {
            for (Iterator<String> iter = infoMessages.iterator(); iter.hasNext(); ) {
                if (((String) iter.next()).indexOf(substring) != -1)
                    return true;
            }
        }
        return false;

    }

    public int numberOfMessagesLogged() {
        return infoMessages.size();
    }

    public void setUp(Level level) {
        setUp();
        setMinLevel(level);
    }
}
