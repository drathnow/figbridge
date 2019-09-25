package zedi.pacbridge.test;

import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;


public class LoggingMessageStartingWithStringMatcher extends BaseMatcher<LoggingEvent> {

    String startingString;
    
    public LoggingMessageStartingWithStringMatcher(String startingString) {
        this.startingString = startingString;
    }
    
    public boolean matches(Object object) {
        LoggingEvent event = (LoggingEvent)object;
        return ((String)event.getMessage()).startsWith(startingString); 
    }

    public void describeTo(Description description) {
        description.appendText("Logging message starting with '" + startingString + "'");
    }
}