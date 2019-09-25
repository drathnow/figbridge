package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class IntegerSystemPropertyTest extends BaseTestCase {

    private static final Integer VALUE = new Integer(100);
    private static final String PROPERTY_NAME = "testProperty";
    private static final Integer MAX_VALUE = new Integer(200);
    private static final Integer MIN_VALUE = new Integer(0);
    private static final Integer DEF_VALUE = new Integer(150);
    private static final Integer TOO_BIG_VALUE = new Integer(500);
    private static final Integer TOO_SMALL_VALUE = new Integer(-1);

    @Test
    public void testCurrentValueTooSmall() throws Exception {
        IntegerSystemProperty systemProperty = new IntegerSystemProperty(PROPERTY_NAME, DEF_VALUE, MIN_VALUE, MAX_VALUE);
        System.setProperty(PROPERTY_NAME, TOO_SMALL_VALUE.toString());
        assertEquals(DEF_VALUE,systemProperty.currentValue());
    }

    @Test
    public void testCurrentValueTooBig() throws Exception {
        IntegerSystemProperty systemProperty = new IntegerSystemProperty(PROPERTY_NAME, DEF_VALUE, MIN_VALUE, MAX_VALUE);
        System.setProperty(PROPERTY_NAME, TOO_BIG_VALUE.toString());
        systemProperty.currentValue();
        assertEquals(DEF_VALUE,systemProperty.currentValue());
    }

    @Test
    public void testCurrentValueWithNoMaxValue() throws Exception {
        IntegerSystemProperty systemProperty = new IntegerSystemProperty(PROPERTY_NAME, DEF_VALUE, MIN_VALUE, null);
     
        System.setProperty(PROPERTY_NAME, TOO_BIG_VALUE.toString());
        assertEquals(TOO_BIG_VALUE.intValue(), systemProperty.currentValue().intValue());
        
    }
    
    @Test
    public void testCurrentValueWithNoMinValue() throws Exception {
        IntegerSystemProperty systemProperty = new IntegerSystemProperty(PROPERTY_NAME, DEF_VALUE, null, MAX_VALUE);
     
        System.setProperty(PROPERTY_NAME, TOO_SMALL_VALUE.toString());
        assertEquals(TOO_SMALL_VALUE.intValue(), systemProperty.currentValue().intValue());
        
    }
    

    @Test
    public void testCurrentValue() throws Exception {
        IntegerSystemProperty systemProperty = new IntegerSystemProperty(PROPERTY_NAME, DEF_VALUE, MIN_VALUE, MAX_VALUE);

        assertEquals(DEF_VALUE.intValue(), systemProperty.currentValue().intValue());

        System.setProperty(PROPERTY_NAME, VALUE.toString());
        assertEquals(VALUE.intValue(), systemProperty.currentValue().intValue());

    }
}
