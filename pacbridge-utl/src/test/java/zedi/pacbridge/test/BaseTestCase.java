package zedi.pacbridge.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import zedi.pacbridge.utl.Utilities;

public abstract class BaseTestCase {

    static {
        if (!Logger.getRootLogger().getAllAppenders().hasMoreElements())
            Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
        Utilities.loadUserBuildProperties();
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
        Mockito.validateMockitoUsage();
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
