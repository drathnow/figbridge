package zedi.pacbridge.utl;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerSystemProperty extends NumericSystemProperty {

    private static Logger logger = LoggerFactory.getLogger(IntegerSystemProperty.class);
    
    public IntegerSystemProperty(String propertyName, Number defaultValue) {
        this(propertyName, defaultValue, null, null);
    }
    
    public IntegerSystemProperty(String propertyName, Number defaultValue, Number minValue) {
        this(propertyName, defaultValue, minValue, null);
    }

    public IntegerSystemProperty(String propertyName, Number defaultValue, Number minValue, Number maxValue) {
        super(propertyName, Integer.class, defaultValue, minValue, maxValue);
    }
    
    public Integer currentValue() {
        String stringValue = System.getProperty(propertyName,defaultValue.toString());
        try {
            Constructor<?> constructor = constructorForClass();
            Integer intValue = (Integer)constructor.newInstance(new Object[] {stringValue});
            return verifiedValue(intValue).intValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value specified for property " 
                    + propertyName + "(" + stringValue + ")" 
                    + ". Default value " 
                    + defaultValue.toString() 
                    +" will be used");
        } catch (Exception e) {
            throw new RuntimeException("Unable to get value for property named '" + propertyName + "'",e);
        }
    }

    protected Number verifiedValue(Number value) {
        if ((minValue != null && value.longValue() < minValue.longValue()) 
                || (maxValue != null && value.longValue() > maxValue.longValue())) {
            logger.warn("Invalid numeric value specified for property " 
                    + propertyName + "(" + value + ")" 
                    + ". Default value " 
                    + defaultValue.toString() 
                    +" will be used");
            value = defaultValue;
        }
        return value;
    }

    public static int valueOf(String propertyName, int defaultValue) {
        String value = System.getProperty(propertyName);
        return (StringUtilities.isNumericString(value)) ? Integer.parseInt(value) : defaultValue;
    }

    public static Integer currentValue(String propertyName, Number defaultValue, Number minValue) {
        return currentValue(propertyName, defaultValue, minValue, null);
    }

    public static Integer currentValue(String propertyName, Number defaultValue, Number minValue, Number maxValue) {
        String stringValue = System.getProperty(propertyName,defaultValue.toString());
        try {
            Constructor<Integer> constructor = Integer.class.getConstructor(new Class[] {String.class});
            Integer intValue = (Integer)constructor.newInstance(new Object[] {stringValue});
            return (Integer)verifiedValue(propertyName, intValue, defaultValue, minValue, maxValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value specified for property " 
                    + propertyName + "(" + stringValue + ")" 
                    + ". Default value " 
                    + defaultValue.toString() 
                    +" will be used");
        } catch (Exception e) {
            throw new RuntimeException("Unable to get value for property named '" + propertyName + "'",e);
        }
    }
    
    private static Number verifiedValue(String propertyName, Number value, Number defaultValue, Number minValue, Number maxValue) {
        if ((minValue != null && value.longValue() < minValue.longValue()) 
                || (maxValue != null && value.longValue() > maxValue.longValue())) {
            logger.warn("Invalid numeric value specified for property " 
                    + propertyName + "(" + value + ")" 
                    + ". Default value " 
                    + defaultValue.toString() 
                    +" will be used");
            value = defaultValue;
        }
        return value;
    }

}
