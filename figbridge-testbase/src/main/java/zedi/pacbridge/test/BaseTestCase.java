package zedi.pacbridge.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

import javax.swing.text.Utilities;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseTestCase.class.getName());

    static {
        if (!org.apache.log4j.Logger.getRootLogger().getAllAppenders().hasMoreElements())
            org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
    }
        
    private Properties savedSystemProperties = new Properties();
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        saveSystemProperties();
    }

    @After
    public void tearDown() throws Exception {
        restoreSystemProperties();
    }
        
    private void saveSystemProperties() {
        savedSystemProperties.putAll(System.getProperties());
    }

    public void assertEqualDates(Date expectedDate, Date compareDate, long allowableDifferenceMilliSeconds) {
        assertEquals(expectedDate.getTime(), compareDate.getTime(), allowableDifferenceMilliSeconds);
    }

    private void restoreSystemProperties() {
        if (savedSystemProperties.size() > 0) {
            System.getProperties().clear();
            System.getProperties().putAll(savedSystemProperties);
            savedSystemProperties.clear();
        }
    }

    public static StartsWithStringMatcher startWithString(String string) {
        return new StartsWithStringMatcher(string);
    }
    
    public static LoggingMessageContainingStringMatcher matchesLoggingMessageContaining(String string) {
        return new LoggingMessageContainingStringMatcher(string);
    }
 
    public static LoggingMessageStartingWithStringMatcher matchesLoggingMessageStartingWith(String startString) {
        return new LoggingMessageStartingWithStringMatcher(startString);
    }
    
    protected void setFieldNamedToValueInObject(String fieldName, Object value, Object object) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
    
}
