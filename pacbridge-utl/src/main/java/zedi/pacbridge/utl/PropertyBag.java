package zedi.pacbridge.utl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyBag {
    private static Logger logger = LoggerFactory.getLogger(PropertyBag.class.getName());
    private Properties properties;
    
    public PropertyBag() {
        this(new Properties());
    }
    
    public PropertyBag(Properties properties) {
        this.properties = properties;
    }
    
    public Integer integerValueForProperty(String propertyName, Integer defaultValue) {
        return integerValueForProperty(propertyName, defaultValue, null, null);
    }
    
    public Integer integerValueForProperty(String propertyName, Integer defaultValue, Integer minValue) {
        return integerValueForProperty(propertyName, defaultValue, minValue, null);
    }
    
    public Integer integerValueForProperty(String propertyName, Integer defaultValue, Integer minValue, Integer maxValue) {
        Integer value = null;
        if (properties.containsKey(propertyName))
            value = new Integer(properties.getProperty(propertyName));
        else if (System.getProperties().containsKey(propertyName))
            value = new Integer(System.getProperty(propertyName));
        return value == null ? defaultValue : PropertyBag.verifiedValue(propertyName, value, defaultValue, minValue, maxValue);
    }
    
    private static <T extends Number> T verifiedValue(String propertyName, T value, T defaultValue, T minValue, T maxValue) {
        if (minValue != null && value.longValue() < minValue.longValue()) {
            logger.warn("Value "
                    + "'" + value + "'"  
                    + " for property '" 
                    + propertyName 
                    + "' is below min value. Min value " 
                    + defaultValue.toString() 
                    +" will be used");
            value = minValue;
        } else if (maxValue != null && value.longValue() > maxValue.longValue()) {
            logger.warn("Value "
                    + "'" + value + "'"  
                    + " for property '" 
                    + propertyName
                    + "' exceeds max value. Max value " 
                    + defaultValue.toString() 
                    +" will be used");
            value = maxValue;
        }
        return value;
    }
}
