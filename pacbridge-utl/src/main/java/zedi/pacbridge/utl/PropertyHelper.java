package zedi.pacbridge.utl;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyHelper {

    private static final Pattern nestedPropertyRE = Pattern.compile("\\$\\{([^}]*)\\}");
    
    private Properties properties;
    
    public PropertyHelper() {
        this(System.getProperties());
    }
    
    public PropertyHelper(Properties properties) {
        this.properties = properties;
    }

    public void defineProperty(String name, String value) {
        properties.setProperty(name, doSubstitution(value));
    }

    private String doSubstitution(String propertyString) {
        Matcher matcher = nestedPropertyRE.matcher(propertyString);
        while (matcher.find()) {
            String nextSub = matcher.group(1);
            String value = propertyValueFromPropertiesInSearchOrder(nextSub);
            if (value != null)
                propertyString = propertyString.replace("${"+nextSub+"}", value);
        }
        return propertyString;
    }
    
    private String propertyValueFromPropertiesInSearchOrder(String propertyName) {
        String value = properties.getProperty(propertyName);
        return value == null ? System.getProperty(propertyName) : value;
    }
}
