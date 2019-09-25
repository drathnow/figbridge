package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class PropertyHelperTest extends BaseTestCase {

    @Test
    public void shouldResolveFromLocalPropertiesThenSystem() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("foo", "local");
        System.setProperty("foo", "bogus");
        System.setProperty("bar", "system");

        PropertyHelper helper = new PropertyHelper(properties);
        
        helper.defineProperty("prop", "${foo}.${bar}");
        
        assertEquals("local.system", properties.getProperty("prop"));
    }
    
    @Test
    public void shouldDefinePropertyWithMultipleNestedProperty() throws Exception {
        PropertyHelper helper = new PropertyHelper();
        assertNull(System.getProperty("foo"));
        helper.defineProperty("foo", "bar");
        helper.defineProperty("prop1", "value1");

        helper.defineProperty("spooge", "${foo}.${prop1}");
        assertEquals("bar.value1", System.getProperty("spooge"));
    }
    
    @Test
    public void shouldDefinePropertyWithNestedProperty() throws Exception {
        PropertyHelper helper = new PropertyHelper();
        assertNull(System.getProperty("foo"));
        helper.defineProperty("foo", "bar");

        helper.defineProperty("spooge", "${foo}.spooge");
        assertEquals("bar.spooge", System.getProperty("spooge"));
        
        helper.defineProperty("spooge", "spooge.${foo}");
        assertEquals("spooge.bar", System.getProperty("spooge"));
        
        helper.defineProperty("foo.bar", "hello");
        helper.defineProperty("prop1", "${foo.bar}.world");
        assertEquals("hello.world", System.getProperty("prop1"));
    }
    
    @Test
    public void shouldDefineSimpleProperty() throws Exception {
        PropertyHelper helper = new PropertyHelper();
        assertNull(System.getProperty("foo"));
        helper.defineProperty("foo", "bar");
        assertNotNull(System.getProperty("foo"));
    }
}
