package zedi.pacbridge.utl;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Utility class to convert strings to objects.
 * @author daver
 *
 */
public class StringToObjectConverter {

    public static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?[0-9]*");
    public static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]");
    
    /**
     * Converts a string to either {@link java.lang.Integer} or {@link java.lang.Float} or returns a {@link java.lang.String}
     * 
     * @param string  a {@link java.lang.String}
     * @return a {@link java.lang.Number} ({@link java.lang.Integer} of {@link java.lang.Float}) or {@link java.lang.String}
     */
    public static Object objectForString(String string) {
        if (INTEGER_PATTERN.matcher(string).matches())
            return new Integer(string);
        if (FLOAT_PATTERN.matcher(string).matches())
            return new Float(string);
        return string;
    }
    
    /**
     * Converts a {@java.util.Map} composed of key:value pairs (where both are strings) to 
     * a {@java.util.Map} of key:value, where keys are string and values are {@link java.lang.Number} ({@link java.lang.Integer}
     * or {@link java.lang.Float}), or {@link java.lang.String}
     * @param properties
     * @return
     */
    public Map<String, Object> convertedProperties(Map<String, String> properties) {
        Map<String, Object> map = new TreeMap<String, Object>();
        for (String key : properties.keySet())
            map.put(key, objectForString(properties.get(key)));
        return map;
    }

    /**
     * Converts a {@java.util.Properties} composed of key:value pairs (where both are strings) to 
     * a {@java.util.Map} of key:value, where keys are string and values are {@link java.lang.Number} ({@link java.lang.Integer}
     * or {@link java.lang.Float}), or {@link java.lang.String}
     * @param properties
     * @return
     */
    public Map<String, Object> convertedProperties(Properties properties) {
        Map<String, Object> map = new TreeMap<String, Object>();
        for (Object key : properties.keySet())
            map.put(key.toString(), objectForString(properties.get(key).toString()));
        return map;
    }
}
