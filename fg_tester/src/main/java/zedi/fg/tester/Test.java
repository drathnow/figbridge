package zedi.fg.tester;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelMatchFilter;

import zedi.fg.tester.ui.ConsoleTextPane;
import zedi.fg.tester.ui.TextPaneAppender;

public class Test
{
    private static final Logger logger = Logger.getLogger(Test.class);

//    public LoggingEvent(String fqnOfCategoryClass, Category logger,
//                      Priority level, Object message, Throwable throwable) {
    public static void main(String[] args)
    {
        System.out.println("Hello world");
        LoggingEvent event = new LoggingEvent("foo.man.choo",
                                              logger, 
                                              System.currentTimeMillis(), 
                                              Level.TRACE, 
                                              "Hello World", 
                                              Thread.currentThread().getName(), 
                                              null, 
                                              "foo", 
                                              new LocationInfo(null, null), 
                                              null);
        TextPaneAppender appender = new TextPaneAppender(new ConsoleTextPane(), new PatternLayout("%m%n"));
        
        LevelMatchFilter filter = new LevelMatchFilter();
        filter.setLevelToMatch("TRACE");
        filter.setAcceptOnMatch(true);;
        appender.addFilter(filter);
        
        appender.doAppend(event);
        logger.addAppender(appender);
        logger.trace("hello World");
    }
}
