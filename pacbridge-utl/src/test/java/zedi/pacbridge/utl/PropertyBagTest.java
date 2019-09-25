package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class PropertyBagTest extends BaseTestCase {

    private static final String PROPERTY_NAME = "foo";
    private static final Integer SYSTEM_VALUE = 1000;
    private static final Integer PROPERTY_BAG_VALUE = 10000;
    private static final Integer DEFAULT_VALUE = 100;
    private static final Integer MIN_VALUE = 50;
    private static final Integer MAX_VALUE = 1500;

    @Test
    public void shouldReturnMaxValueIfCurrentValueTooHigh() throws Exception {
        System.setProperty(PROPERTY_NAME, "2000");
        PropertyBag propertyBag = new PropertyBag();
        assertEquals(MAX_VALUE.intValue(), propertyBag.integerValueForProperty(PROPERTY_NAME, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE).intValue());        
    }
    
    @Test
    public void shouldReturnMinValueIfCurrentValueTooLow() throws Exception {
        System.setProperty(PROPERTY_NAME, "20");
        PropertyBag propertyBag = new PropertyBag();
        assertEquals(MIN_VALUE.intValue(), propertyBag.integerValueForProperty(PROPERTY_NAME, DEFAULT_VALUE, MIN_VALUE).intValue());        
    }
    
    @Test
    public void shouldReturnValueFromPropertyBag() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_NAME, ""+PROPERTY_BAG_VALUE);
        PropertyBag propertyBag = new PropertyBag(properties);
        assertEquals(PROPERTY_BAG_VALUE, propertyBag.integerValueForProperty(PROPERTY_NAME, DEFAULT_VALUE));
    }
    
    @Test
    public void shouldReturnDefaultValueWhenNoValudFound() throws Exception {
        PropertyBag propertyBag = new PropertyBag();
        assertEquals(DEFAULT_VALUE, propertyBag.integerValueForProperty(PROPERTY_NAME, DEFAULT_VALUE));
    }
    
    @Test
    public void shouldGetValueFromSystemPropertiesWhenNotProvided() {
        System.setProperty(PROPERTY_NAME, ""+SYSTEM_VALUE);
        PropertyBag propertyBag = new PropertyBag();
        assertEquals(SYSTEM_VALUE.intValue(), propertyBag.integerValueForProperty(PROPERTY_NAME, DEFAULT_VALUE).intValue());
    }

}
