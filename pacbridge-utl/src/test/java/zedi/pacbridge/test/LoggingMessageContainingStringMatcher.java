package zedi.pacbridge.test;

import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;


public class LoggingMessageContainingStringMatcher  extends BaseMatcher<LoggingEvent> {

    String containingString;
    
    public LoggingMessageContainingStringMatcher(String containingString) {
        this.containingString = containingString;
    }
    
    public boolean matches(Object object) {
        LoggingEvent event = (LoggingEvent)object;
        return ((String)event.getMessage()).contains(containingString); 
    }

    public void describeTo(Description description) {
        description.appendText("Logging message containing string '" + containingString + "'");
    }
}